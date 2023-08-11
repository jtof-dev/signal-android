package org.mycrimes.insecuretests.messagerequests;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import org.signal.core.util.Result;
import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.signal.storageservice.protos.groups.local.DecryptedGroup;
import org.mycrimes.insecuretests.database.GroupTable;
import org.mycrimes.insecuretests.database.MessageTable;
import org.mycrimes.insecuretests.database.RecipientTable;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.database.ThreadTable;
import org.mycrimes.insecuretests.database.model.GroupRecord;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.groups.GroupChangeException;
import org.mycrimes.insecuretests.groups.GroupManager;
import org.mycrimes.insecuretests.groups.ui.GroupChangeErrorCallback;
import org.mycrimes.insecuretests.groups.ui.GroupChangeFailureReason;
import org.mycrimes.insecuretests.jobs.MultiDeviceMessageRequestResponseJob;
import org.mycrimes.insecuretests.jobs.ReportSpamJob;
import org.mycrimes.insecuretests.jobs.SendViewedReceiptJob;
import org.mycrimes.insecuretests.notifications.MarkReadReceiver;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.recipients.RecipientId;
import org.mycrimes.insecuretests.recipients.RecipientUtil;
import org.mycrimes.insecuretests.sms.MessageSender;
import org.mycrimes.insecuretests.util.FeatureFlags;
import org.mycrimes.insecuretests.util.TextSecurePreferences;
import org.whispersystems.signalservice.internal.push.exceptions.GroupPatchNotAcceptedException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;

public final class MessageRequestRepository {

  private static final String TAG = Log.tag(MessageRequestRepository.class);

  private final Context  context;
  private final Executor executor;

  public MessageRequestRepository(@NonNull Context context) {
    this.context  = context.getApplicationContext();
    this.executor = SignalExecutors.BOUNDED;
  }

  public void getGroups(@NonNull RecipientId recipientId, @NonNull Consumer<List<String>> onGroupsLoaded) {
    executor.execute(() -> {
      GroupTable groupDatabase = SignalDatabase.groups();
      onGroupsLoaded.accept(groupDatabase.getPushGroupNamesContainingMember(recipientId));
    });
  }

  public void getGroupInfo(@NonNull RecipientId recipientId, @NonNull Consumer<GroupInfo> onGroupInfoLoaded) {
    executor.execute(() -> {
      GroupTable            groupDatabase = SignalDatabase.groups();
      Optional<GroupRecord> groupRecord   = groupDatabase.getGroup(recipientId);
      onGroupInfoLoaded.accept(groupRecord.map(record -> {
        if (record.isV2Group()) {
          DecryptedGroup decryptedGroup = record.requireV2GroupProperties().getDecryptedGroup();
          return new GroupInfo(decryptedGroup.getMembersCount(), decryptedGroup.getPendingMembersCount(), decryptedGroup.getDescription());
        } else {
          return new GroupInfo(record.getMembers().size(), 0, "");
        }
      }).orElse(GroupInfo.ZERO));
    });
  }

  @WorkerThread
  public @NonNull MessageRequestRecipientInfo getRecipientInfo(@NonNull RecipientId recipientId, long threadId) {
    List<String>          sharedGroups = SignalDatabase.groups().getPushGroupNamesContainingMember(recipientId);
    Optional<GroupRecord> groupRecord  = SignalDatabase.groups().getGroup(recipientId);
    GroupInfo             groupInfo    = GroupInfo.ZERO;

    if (groupRecord.isPresent()) {
      if (groupRecord.get().isV2Group()) {
        DecryptedGroup decryptedGroup = groupRecord.get().requireV2GroupProperties().getDecryptedGroup();
        groupInfo = new GroupInfo(decryptedGroup.getMembersCount(), decryptedGroup.getPendingMembersCount(), decryptedGroup.getDescription());
      } else {
        groupInfo = new GroupInfo(groupRecord.get().getMembers().size(), 0, "");
      }
    }

    Recipient recipient = Recipient.resolved(recipientId);

    return new MessageRequestRecipientInfo(
        recipient,
        groupInfo,
        sharedGroups,
        getMessageRequestState(recipient, threadId)
    );
  }

  @WorkerThread
  public @NonNull MessageRequestState getMessageRequestState(@NonNull Recipient recipient, long threadId) {
    if (recipient.isBlocked()) {
      if (recipient.isGroup()) {
        return MessageRequestState.BLOCKED_GROUP;
      } else {
        return MessageRequestState.BLOCKED_INDIVIDUAL;
      }
    } else if (threadId <= 0) {
      return MessageRequestState.NONE;
    } else if (recipient.isPushV2Group()) {
      switch (getGroupMemberLevel(recipient.getId())) {
        case NOT_A_MEMBER:
          return MessageRequestState.NONE;
        case PENDING_MEMBER:
          return MessageRequestState.GROUP_V2_INVITE;
        default:
          if (RecipientUtil.isMessageRequestAccepted(context, threadId)) {
            return MessageRequestState.NONE;
          } else {
            return MessageRequestState.GROUP_V2_ADD;
          }
      }
    } else if (!RecipientUtil.isLegacyProfileSharingAccepted(recipient) && isLegacyThread(recipient)) {
      if (recipient.isGroup()) {
        return MessageRequestState.LEGACY_GROUP_V1;
      } else {
        return MessageRequestState.LEGACY_INDIVIDUAL;
      }
    } else if (recipient.isPushV1Group()) {
      if (RecipientUtil.isMessageRequestAccepted(context, threadId)) {
        if (recipient.getParticipantIds().size() > FeatureFlags.groupLimits().getHardLimit()) {
          return MessageRequestState.DEPRECATED_GROUP_V1_TOO_LARGE;
        } else {
          return MessageRequestState.DEPRECATED_GROUP_V1;
        }
      } else if (!recipient.isActiveGroup()) {
        return MessageRequestState.NONE;
      } else {
        return MessageRequestState.GROUP_V1;
      }
    } else {
      if (RecipientUtil.isMessageRequestAccepted(context, threadId)) {
        return MessageRequestState.NONE;
      } else if (RecipientUtil.isRecipientHidden(threadId)) {
        return MessageRequestState.INDIVIDUAL_HIDDEN;
      } else {
        return MessageRequestState.INDIVIDUAL;
      }
    }
  }

  @SuppressWarnings("unchecked")
  public @NonNull Single<Result<Unit, GroupChangeFailureReason>> acceptMessageRequest(@NonNull RecipientId recipientId, long threadId) {
    //noinspection CodeBlock2Expr
    return Single.<Result<Unit, GroupChangeFailureReason>>create(emitter -> {
      acceptMessageRequest(
          recipientId,
          threadId,
          () -> emitter.onSuccess(Result.success(Unit.INSTANCE)),
          reason -> emitter.onSuccess(Result.failure(reason))
      );
    }).subscribeOn(Schedulers.io());
  }

  public void acceptMessageRequest(@NonNull RecipientId recipientId,
                                   long threadId,
                                   @NonNull Runnable onMessageRequestAccepted,
                                   @NonNull GroupChangeErrorCallback error)
  {
    executor.execute(()-> {
      Recipient recipient = Recipient.resolved(recipientId);
      if (recipient.isPushV2Group()) {
        try {
          Log.i(TAG, "GV2 accepting invite");
          GroupManager.acceptInvite(context, recipient.requireGroupId().requireV2());

          RecipientTable recipientTable = SignalDatabase.recipients();
          recipientTable.setProfileSharing(recipientId, true);

          onMessageRequestAccepted.run();
        } catch (GroupChangeException | IOException e) {
          Log.w(TAG, e);
          error.onError(GroupChangeFailureReason.fromException(e));
        }
      } else {
        RecipientTable recipientTable = SignalDatabase.recipients();
        recipientTable.setProfileSharing(recipientId, true);

        MessageSender.sendProfileKey(threadId);

        List<MessageTable.MarkedMessageInfo> messageIds = SignalDatabase.threads().setEntireThreadRead(threadId);
        ApplicationDependencies.getMessageNotifier().updateNotification(context);
        MarkReadReceiver.process(context, messageIds);

        List<MessageTable.MarkedMessageInfo> viewedInfos = SignalDatabase.messages().getViewedIncomingMessages(threadId);

        SendViewedReceiptJob.enqueue(threadId, recipientId, viewedInfos);

        if (TextSecurePreferences.isMultiDevice(context)) {
          ApplicationDependencies.getJobManager().add(MultiDeviceMessageRequestResponseJob.forAccept(recipientId));
        }

        onMessageRequestAccepted.run();
      }
    });
  }

  @SuppressWarnings("unchecked")
  public @NonNull Single<Result<Unit, GroupChangeFailureReason>> deleteMessageRequest(@NonNull RecipientId recipientId, long threadId) {
    //noinspection CodeBlock2Expr
    return Single.<Result<Unit, GroupChangeFailureReason>>create(emitter -> {
      deleteMessageRequest(
          recipientId,
          threadId,
          () -> emitter.onSuccess(Result.success(Unit.INSTANCE)),
          reason -> emitter.onSuccess(Result.failure(reason))
      );
    }).subscribeOn(Schedulers.io());
  }

  public void deleteMessageRequest(@NonNull RecipientId recipientId,
                                   long threadId,
                                   @NonNull Runnable onMessageRequestDeleted,
                                   @NonNull GroupChangeErrorCallback error)
  {
    executor.execute(() -> {
      Recipient resolved = Recipient.resolved(recipientId);

      if (resolved.isGroup() && resolved.requireGroupId().isPush()) {
        try {
          GroupManager.leaveGroupFromBlockOrMessageRequest(context, resolved.requireGroupId().requirePush());
        } catch (GroupChangeException | GroupPatchNotAcceptedException e) {
          if (SignalDatabase.groups().isCurrentMember(resolved.requireGroupId().requirePush(), Recipient.self().getId())) {
            Log.w(TAG, "Failed to leave group, and we're still a member.", e);
            error.onError(GroupChangeFailureReason.fromException(e));
            return;
          } else {
            Log.w(TAG, "Failed to leave group, but we're not a member, so ignoring.");
          }
        } catch (IOException e) {
          Log.w(TAG, e);
          error.onError(GroupChangeFailureReason.fromException(e));
          return;
        }
      }

      if (TextSecurePreferences.isMultiDevice(context)) {
        ApplicationDependencies.getJobManager().add(MultiDeviceMessageRequestResponseJob.forDelete(recipientId));
      }

      ThreadTable threadTable = SignalDatabase.threads();
      threadTable.deleteConversation(threadId);

      onMessageRequestDeleted.run();
    });
  }

  @SuppressWarnings("unchecked")
  public @NonNull Single<Result<Unit, GroupChangeFailureReason>> blockMessageRequest(@NonNull RecipientId recipientId) {
    //noinspection CodeBlock2Expr
    return Single.<Result<Unit, GroupChangeFailureReason>>create(emitter -> {
      blockMessageRequest(
          recipientId,
          () -> emitter.onSuccess(Result.success(Unit.INSTANCE)),
          reason -> emitter.onSuccess(Result.failure(reason))
      );
    }).subscribeOn(Schedulers.io());
  }

  public void blockMessageRequest(@NonNull RecipientId recipientId,
                                  @NonNull Runnable onMessageRequestBlocked,
                                  @NonNull GroupChangeErrorCallback error)
  {
    executor.execute(() -> {
      Recipient recipient = Recipient.resolved(recipientId);
      try {
        RecipientUtil.block(context, recipient);
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
        return;
      }
      Recipient.live(recipientId).refresh();

      if (TextSecurePreferences.isMultiDevice(context)) {
        ApplicationDependencies.getJobManager().add(MultiDeviceMessageRequestResponseJob.forBlock(recipientId));
      }

      onMessageRequestBlocked.run();
    });
  }

  @SuppressWarnings("unchecked")
  public @NonNull Single<Result<Unit, GroupChangeFailureReason>> blockAndReportSpamMessageRequest(@NonNull RecipientId recipientId, long threadId) {
    //noinspection CodeBlock2Expr
    return Single.<Result<Unit, GroupChangeFailureReason>>create(emitter -> {
      blockAndReportSpamMessageRequest(
          recipientId,
          threadId,
          () -> emitter.onSuccess(Result.success(Unit.INSTANCE)),
          reason -> emitter.onSuccess(Result.failure(reason))
      );
    }).subscribeOn(Schedulers.io());
  }

  public void blockAndReportSpamMessageRequest(@NonNull RecipientId recipientId,
                                               long threadId,
                                               @NonNull Runnable onMessageRequestBlocked,
                                               @NonNull GroupChangeErrorCallback error)
  {
    executor.execute(() -> {
      Recipient recipient = Recipient.resolved(recipientId);
      try{
        RecipientUtil.block(context, recipient);
      } catch (GroupChangeException | IOException e) {
        Log.w(TAG, e);
        error.onError(GroupChangeFailureReason.fromException(e));
        return;
      }
      Recipient.live(recipientId).refresh();

      ApplicationDependencies.getJobManager().add(new ReportSpamJob(threadId, System.currentTimeMillis()));

      if (TextSecurePreferences.isMultiDevice(context)) {
        ApplicationDependencies.getJobManager().add(MultiDeviceMessageRequestResponseJob.forBlockAndReportSpam(recipientId));
      }

      onMessageRequestBlocked.run();
    });
  }

  @SuppressWarnings("unchecked")
  public @NonNull Single<Result<Unit, GroupChangeFailureReason>> unblockAndAccept(@NonNull RecipientId recipientId) {
    //noinspection CodeBlock2Expr
    return Single.<Result<Unit, GroupChangeFailureReason>>create(emitter -> {
      unblockAndAccept(
          recipientId,
          () -> emitter.onSuccess(Result.success(Unit.INSTANCE))
      );
    }).subscribeOn(Schedulers.io());
  }

  public void unblockAndAccept(@NonNull RecipientId recipientId, @NonNull Runnable onMessageRequestUnblocked) {
    executor.execute(() -> {
      Recipient recipient = Recipient.resolved(recipientId);

      RecipientUtil.unblock(recipient);

      if (TextSecurePreferences.isMultiDevice(context)) {
        ApplicationDependencies.getJobManager().add(MultiDeviceMessageRequestResponseJob.forAccept(recipientId));
      }

      onMessageRequestUnblocked.run();
    });
  }

  private GroupTable.MemberLevel getGroupMemberLevel(@NonNull RecipientId recipientId) {
    return SignalDatabase.groups()
                          .getGroup(recipientId)
                          .map(g -> g.memberLevel(Recipient.self()))
                          .orElse(GroupTable.MemberLevel.NOT_A_MEMBER);
  }


  @WorkerThread
  private boolean isLegacyThread(@NonNull Recipient recipient) {
    Context context  = ApplicationDependencies.getApplication();
    Long    threadId = SignalDatabase.threads().getThreadIdFor(recipient.getId());

    return threadId != null &&
        (RecipientUtil.hasSentMessageInThread(threadId) || RecipientUtil.isPreMessageRequestThread(threadId));
  }
}