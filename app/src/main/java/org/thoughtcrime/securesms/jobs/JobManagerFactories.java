package org.mycrimes.insecuretests.jobs;

import android.app.Application;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.jobmanager.Constraint;
import org.mycrimes.insecuretests.jobmanager.ConstraintObserver;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.JobMigration;
import org.mycrimes.insecuretests.jobmanager.impl.AutoDownloadEmojiConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.CellServiceConstraintObserver;
import org.mycrimes.insecuretests.jobmanager.impl.ChangeNumberConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.ChargingConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.ChargingConstraintObserver;
import org.mycrimes.insecuretests.jobmanager.impl.DecryptionsDrainedConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.DecryptionsDrainedConstraintObserver;
import org.mycrimes.insecuretests.jobmanager.impl.NetworkConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.NetworkConstraintObserver;
import org.mycrimes.insecuretests.jobmanager.impl.NetworkOrCellServiceConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.NotInCallConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.NotInCallConstraintObserver;
import org.mycrimes.insecuretests.jobmanager.impl.SqlCipherMigrationConstraint;
import org.mycrimes.insecuretests.jobmanager.impl.SqlCipherMigrationConstraintObserver;
import org.mycrimes.insecuretests.jobmanager.migrations.PushDecryptMessageJobEnvelopeMigration;
import org.mycrimes.insecuretests.jobmanager.migrations.PushProcessMessageQueueJobMigration;
import org.mycrimes.insecuretests.jobmanager.migrations.RecipientIdFollowUpJobMigration;
import org.mycrimes.insecuretests.jobmanager.migrations.RecipientIdFollowUpJobMigration2;
import org.mycrimes.insecuretests.jobmanager.migrations.RecipientIdJobMigration;
import org.mycrimes.insecuretests.jobmanager.migrations.RetrieveProfileJobMigration;
import org.mycrimes.insecuretests.jobmanager.migrations.SendReadReceiptsJobMigration;
import org.mycrimes.insecuretests.jobmanager.migrations.SenderKeyDistributionSendJobRecipientMigration;
import org.mycrimes.insecuretests.migrations.AccountConsistencyMigrationJob;
import org.mycrimes.insecuretests.migrations.AccountRecordMigrationJob;
import org.mycrimes.insecuretests.migrations.ApplyUnknownFieldsToSelfMigrationJob;
import org.mycrimes.insecuretests.migrations.AttachmentCleanupMigrationJob;
import org.mycrimes.insecuretests.migrations.AttributesMigrationJob;
import org.mycrimes.insecuretests.migrations.AvatarIdRemovalMigrationJob;
import org.mycrimes.insecuretests.migrations.AvatarMigrationJob;
import org.mycrimes.insecuretests.migrations.BackupJitterMigrationJob;
import org.mycrimes.insecuretests.migrations.BackupNotificationMigrationJob;
import org.mycrimes.insecuretests.migrations.BlobStorageLocationMigrationJob;
import org.mycrimes.insecuretests.migrations.CachedAttachmentsMigrationJob;
import org.mycrimes.insecuretests.migrations.ClearGlideCacheMigrationJob;
import org.mycrimes.insecuretests.migrations.DatabaseMigrationJob;
import org.mycrimes.insecuretests.migrations.DecryptionsDrainedMigrationJob;
import org.mycrimes.insecuretests.migrations.DeleteDeprecatedLogsMigrationJob;
import org.mycrimes.insecuretests.migrations.DirectoryRefreshMigrationJob;
import org.mycrimes.insecuretests.migrations.EmojiDownloadMigrationJob;
import org.mycrimes.insecuretests.migrations.KbsEnclaveMigrationJob;
import org.mycrimes.insecuretests.migrations.LegacyMigrationJob;
import org.mycrimes.insecuretests.migrations.MigrationCompleteJob;
import org.mycrimes.insecuretests.migrations.OptimizeMessageSearchIndexMigrationJob;
import org.mycrimes.insecuretests.migrations.PassingMigrationJob;
import org.mycrimes.insecuretests.migrations.PinOptOutMigration;
import org.mycrimes.insecuretests.migrations.PinReminderMigrationJob;
import org.mycrimes.insecuretests.migrations.PniAccountInitializationMigrationJob;
import org.mycrimes.insecuretests.migrations.PniMigrationJob;
import org.mycrimes.insecuretests.migrations.PreKeysSyncMigrationJob;
import org.mycrimes.insecuretests.migrations.ProfileMigrationJob;
import org.mycrimes.insecuretests.migrations.ProfileSharingUpdateMigrationJob;
import org.mycrimes.insecuretests.migrations.RebuildMessageSearchIndexMigrationJob;
import org.mycrimes.insecuretests.migrations.RecipientSearchMigrationJob;
import org.mycrimes.insecuretests.migrations.RegistrationPinV2MigrationJob;
import org.mycrimes.insecuretests.migrations.StickerAdditionMigrationJob;
import org.mycrimes.insecuretests.migrations.StickerDayByDayMigrationJob;
import org.mycrimes.insecuretests.migrations.StickerLaunchMigrationJob;
import org.mycrimes.insecuretests.migrations.StickerMyDailyLifeMigrationJob;
import org.mycrimes.insecuretests.migrations.StorageCapabilityMigrationJob;
import org.mycrimes.insecuretests.migrations.StorageServiceMigrationJob;
import org.mycrimes.insecuretests.migrations.StorageServiceSystemNameMigrationJob;
import org.mycrimes.insecuretests.migrations.StoryReadStateMigrationJob;
import org.mycrimes.insecuretests.migrations.StoryViewedReceiptsStateMigrationJob;
import org.mycrimes.insecuretests.migrations.SyncDistributionListsMigrationJob;
import org.mycrimes.insecuretests.migrations.TrimByLengthSettingsMigrationJob;
import org.mycrimes.insecuretests.migrations.UpdateSmsJobsMigrationJob;
import org.mycrimes.insecuretests.migrations.UserNotificationMigrationJob;
import org.mycrimes.insecuretests.migrations.UuidMigrationJob;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JobManagerFactories {

  public static Map<String, Job.Factory> getJobFactories(@NonNull Application application) {
    return new HashMap<String, Job.Factory>() {{
      put(AccountConsistencyWorkerJob.KEY,           new AccountConsistencyWorkerJob.Factory());
      put(AttachmentCopyJob.KEY,                     new AttachmentCopyJob.Factory());
      put(AttachmentDownloadJob.KEY,                 new AttachmentDownloadJob.Factory());
      put(AttachmentUploadJob.KEY,                   new AttachmentUploadJob.Factory());
      put(AttachmentMarkUploadedJob.KEY,             new AttachmentMarkUploadedJob.Factory());
      put(AttachmentCompressionJob.KEY,              new AttachmentCompressionJob.Factory());
      put(AutomaticSessionResetJob.KEY,              new AutomaticSessionResetJob.Factory());
      put(AvatarGroupsV1DownloadJob.KEY,             new AvatarGroupsV1DownloadJob.Factory());
      put(AvatarGroupsV2DownloadJob.KEY,             new AvatarGroupsV2DownloadJob.Factory());
      put(BoostReceiptRequestResponseJob.KEY,        new BoostReceiptRequestResponseJob.Factory());
      put(CallLinkPeekJob.KEY,                       new CallLinkPeekJob.Factory());
      put("CallSyncEventJob",                        new FailingJob.Factory());
      put(CallSyncEventJob.KEY,                      new CallSyncEventJob.Factory());
      put(CheckServiceReachabilityJob.KEY,           new CheckServiceReachabilityJob.Factory());
      put(CleanPreKeysJob.KEY,                       new CleanPreKeysJob.Factory());
      put(ClearFallbackKbsEnclaveJob.KEY,            new ClearFallbackKbsEnclaveJob.Factory());
      put(ConversationShortcutRankingUpdateJob.KEY,  new ConversationShortcutRankingUpdateJob.Factory());
      put(ConversationShortcutUpdateJob.KEY,         new ConversationShortcutUpdateJob.Factory());
      put(CreateReleaseChannelJob.KEY,               new CreateReleaseChannelJob.Factory());
      put(DirectoryRefreshJob.KEY,                   new DirectoryRefreshJob.Factory());
      put(DonationReceiptRedemptionJob.KEY,          new DonationReceiptRedemptionJob.Factory());
      put(DownloadLatestEmojiDataJob.KEY,            new DownloadLatestEmojiDataJob.Factory());
      put(EmojiSearchIndexDownloadJob.KEY,           new EmojiSearchIndexDownloadJob.Factory());
      put(FcmRefreshJob.KEY,                         new FcmRefreshJob.Factory());
      put(FetchRemoteMegaphoneImageJob.KEY,          new FetchRemoteMegaphoneImageJob.Factory());
      put(FontDownloaderJob.KEY,                     new FontDownloaderJob.Factory());
      put(ForceUpdateGroupV2Job.KEY,                 new ForceUpdateGroupV2Job.Factory());
      put(ForceUpdateGroupV2WorkerJob.KEY,           new ForceUpdateGroupV2WorkerJob.Factory());
      put(GenerateAudioWaveFormJob.KEY,              new GenerateAudioWaveFormJob.Factory());
      put(GiftSendJob.KEY,                           new GiftSendJob.Factory());
      put(GroupV1MigrationJob.KEY,                   new GroupV1MigrationJob.Factory());
      put(GroupCallUpdateSendJob.KEY,                new GroupCallUpdateSendJob.Factory());
      put(GroupCallPeekJob.KEY,                      new GroupCallPeekJob.Factory());
      put(GroupCallPeekWorkerJob.KEY,                new GroupCallPeekWorkerJob.Factory());
      put(GroupV2UpdateSelfProfileKeyJob.KEY,        new GroupV2UpdateSelfProfileKeyJob.Factory());
      put(IndividualSendJob.KEY,                     new IndividualSendJob.Factory());
      put(KbsEnclaveMigrationWorkerJob.KEY,          new KbsEnclaveMigrationWorkerJob.Factory());
      put(LeaveGroupV2Job.KEY,                       new LeaveGroupV2Job.Factory());
      put(LeaveGroupV2WorkerJob.KEY,                 new LeaveGroupV2WorkerJob.Factory());
      put(LocalBackupJob.KEY,                        new LocalBackupJob.Factory());
      put(LocalBackupJobApi29.KEY,                   new LocalBackupJobApi29.Factory());
      put(MarkerJob.KEY,                             new MarkerJob.Factory());
      put(MmsDownloadJob.KEY,                        new MmsDownloadJob.Factory());
      put(MmsReceiveJob.KEY,                         new MmsReceiveJob.Factory());
      put(MmsSendJob.KEY,                            new MmsSendJob.Factory());
      put(MultiDeviceBlockedUpdateJob.KEY,           new MultiDeviceBlockedUpdateJob.Factory());
      put(MultiDeviceCallLinkSyncJob.KEY,            new MultiDeviceCallLinkSyncJob.Factory());
      put(MultiDeviceConfigurationUpdateJob.KEY,     new MultiDeviceConfigurationUpdateJob.Factory());
      put(MultiDeviceContactSyncJob.KEY,             new MultiDeviceContactSyncJob.Factory());
      put(MultiDeviceContactUpdateJob.KEY,           new MultiDeviceContactUpdateJob.Factory());
      put(MultiDeviceKeysUpdateJob.KEY,              new MultiDeviceKeysUpdateJob.Factory());
      put(MultiDeviceMessageRequestResponseJob.KEY,  new MultiDeviceMessageRequestResponseJob.Factory());
      put(MultiDeviceOutgoingPaymentSyncJob.KEY,     new MultiDeviceOutgoingPaymentSyncJob.Factory());
      put(MultiDeviceProfileContentUpdateJob.KEY,    new MultiDeviceProfileContentUpdateJob.Factory());
      put(MultiDeviceProfileKeyUpdateJob.KEY,        new MultiDeviceProfileKeyUpdateJob.Factory());
      put(MultiDeviceReadUpdateJob.KEY,              new MultiDeviceReadUpdateJob.Factory());
      put(MultiDeviceStickerPackOperationJob.KEY,    new MultiDeviceStickerPackOperationJob.Factory());
      put(MultiDeviceStickerPackSyncJob.KEY,         new MultiDeviceStickerPackSyncJob.Factory());
      put(MultiDeviceStorageSyncRequestJob.KEY,      new MultiDeviceStorageSyncRequestJob.Factory());
      put(MultiDeviceSubscriptionSyncRequestJob.KEY, new MultiDeviceSubscriptionSyncRequestJob.Factory());
      put(MultiDeviceVerifiedUpdateJob.KEY,          new MultiDeviceVerifiedUpdateJob.Factory());
      put(MultiDeviceViewOnceOpenJob.KEY,            new MultiDeviceViewOnceOpenJob.Factory());
      put(MultiDeviceViewedUpdateJob.KEY,            new MultiDeviceViewedUpdateJob.Factory());
      put(NewRegistrationUsernameSyncJob.KEY,        new NewRegistrationUsernameSyncJob.Factory());
      put(NullMessageSendJob.KEY,                    new NullMessageSendJob.Factory());
      put(OptimizeMessageSearchIndexJob.KEY,         new OptimizeMessageSearchIndexJob.Factory());
      put(PaymentLedgerUpdateJob.KEY,                new PaymentLedgerUpdateJob.Factory());
      put(PaymentNotificationSendJob.KEY,            new PaymentNotificationSendJob.Factory());
      put(PaymentNotificationSendJobV2.KEY,          new PaymentNotificationSendJobV2.Factory());
      put(PaymentSendJob.KEY,                        new PaymentSendJob.Factory());
      put(PaymentTransactionCheckJob.KEY,            new PaymentTransactionCheckJob.Factory());
      put(PnpInitializeDevicesJob.KEY,               new PnpInitializeDevicesJob.Factory());
      put(PreKeysSyncJob.KEY,                        new PreKeysSyncJob.Factory());
      put(ProfileKeySendJob.KEY,                     new ProfileKeySendJob.Factory());
      put(ProfileUploadJob.KEY,                      new ProfileUploadJob.Factory());
      put(PushDecryptMessageJob.KEY,                 new PushDecryptMessageJob.Factory());
      put(PushDecryptDrainedJob.KEY,                 new PushDecryptDrainedJob.Factory());
      put(PushDistributionListSendJob.KEY,           new PushDistributionListSendJob.Factory());
      put(PushGroupSendJob.KEY,                      new PushGroupSendJob.Factory());
      put(PushGroupSilentUpdateSendJob.KEY,          new PushGroupSilentUpdateSendJob.Factory());
      put(PushNotificationReceiveJob.KEY,            new PushNotificationReceiveJob.Factory());
      put(PushProcessEarlyMessagesJob.KEY,           new PushProcessEarlyMessagesJob.Factory());
      put(PushProcessMessageJob.KEY,                 new PushProcessMessageJob.Factory());
      put(PushProcessMessageJobV2.KEY,               new PushProcessMessageJobV2.Factory());
      put(ReactionSendJob.KEY,                       new ReactionSendJob.Factory());
      put(RebuildMessageSearchIndexJob.KEY,          new RebuildMessageSearchIndexJob.Factory());
      put(RefreshAttributesJob.KEY,                  new RefreshAttributesJob.Factory());
      put(RefreshCallLinkDetailsJob.KEY,             new RefreshCallLinkDetailsJob.Factory());
      put(RefreshKbsCredentialsJob.KEY,              new RefreshKbsCredentialsJob.Factory());
      put(RefreshOwnProfileJob.KEY,                  new RefreshOwnProfileJob.Factory());
      put(RemoteConfigRefreshJob.KEY,                new RemoteConfigRefreshJob.Factory());
      put(RemoteDeleteSendJob.KEY,                   new RemoteDeleteSendJob.Factory());
      put(ReportSpamJob.KEY,                         new ReportSpamJob.Factory());
      put(ResendMessageJob.KEY,                      new ResendMessageJob.Factory());
      put(ResumableUploadSpecJob.KEY,                new ResumableUploadSpecJob.Factory());
      put(RequestGroupV2InfoWorkerJob.KEY,           new RequestGroupV2InfoWorkerJob.Factory());
      put(RequestGroupV2InfoJob.KEY,                 new RequestGroupV2InfoJob.Factory());
      put(RetrieveProfileAvatarJob.KEY,              new RetrieveProfileAvatarJob.Factory());
      put(RetrieveProfileJob.KEY,                    new RetrieveProfileJob.Factory());
      put(RetrieveRemoteAnnouncementsJob.KEY,        new RetrieveRemoteAnnouncementsJob.Factory());
      put(RotateCertificateJob.KEY,                  new RotateCertificateJob.Factory());
      put(RotateProfileKeyJob.KEY,                   new RotateProfileKeyJob.Factory());
      put(SenderKeyDistributionSendJob.KEY,          new SenderKeyDistributionSendJob.Factory());
      put(SendDeliveryReceiptJob.KEY,                new SendDeliveryReceiptJob.Factory());
      put(SendPaymentsActivatedJob.KEY,              new SendPaymentsActivatedJob.Factory());
      put(SendReadReceiptJob.KEY,                    new SendReadReceiptJob.Factory(application));
      put(SendRetryReceiptJob.KEY,                   new SendRetryReceiptJob.Factory());
      put(SendViewedReceiptJob.KEY,                  new SendViewedReceiptJob.Factory(application));
      put(SyncSystemContactLinksJob.KEY,             new SyncSystemContactLinksJob.Factory());
      put(MultiDeviceStorySendSyncJob.KEY,           new MultiDeviceStorySendSyncJob.Factory());
      put(ServiceOutageDetectionJob.KEY,             new ServiceOutageDetectionJob.Factory());
      put(SmsReceiveJob.KEY,                         new SmsReceiveJob.Factory());
      put(SmsSendJob.KEY,                            new SmsSendJob.Factory());
      put(SmsSentJob.KEY,                            new SmsSentJob.Factory());
      put(StickerDownloadJob.KEY,                    new StickerDownloadJob.Factory());
      put(StickerPackDownloadJob.KEY,                new StickerPackDownloadJob.Factory());
      put(StorageAccountRestoreJob.KEY,              new StorageAccountRestoreJob.Factory());
      put(StorageForcePushJob.KEY,                   new StorageForcePushJob.Factory());
      put(StorageSyncJob.KEY,                        new StorageSyncJob.Factory());
      put(SubscriptionKeepAliveJob.KEY,              new SubscriptionKeepAliveJob.Factory());
      put(SubscriptionReceiptRequestResponseJob.KEY, new SubscriptionReceiptRequestResponseJob.Factory());
      put(StoryOnboardingDownloadJob.KEY,            new StoryOnboardingDownloadJob.Factory());
      put(SubmitRateLimitPushChallengeJob.KEY,       new SubmitRateLimitPushChallengeJob.Factory());
      put(ThreadUpdateJob.KEY,                       new ThreadUpdateJob.Factory());
      put(TrimThreadJob.KEY,                         new TrimThreadJob.Factory());
      put(TypingSendJob.KEY,                         new TypingSendJob.Factory());
      put(UpdateApkJob.KEY,                          new UpdateApkJob.Factory());

      // Migrations
      put(AccountConsistencyMigrationJob.KEY,        new AccountConsistencyMigrationJob.Factory());
      put(AccountRecordMigrationJob.KEY,             new AccountRecordMigrationJob.Factory());
      put(ApplyUnknownFieldsToSelfMigrationJob.KEY,  new ApplyUnknownFieldsToSelfMigrationJob.Factory());
      put(AttachmentCleanupMigrationJob.KEY,         new AttachmentCleanupMigrationJob.Factory());
      put(AttributesMigrationJob.KEY,                new AttributesMigrationJob.Factory());
      put(AvatarIdRemovalMigrationJob.KEY,           new AvatarIdRemovalMigrationJob.Factory());
      put(AvatarMigrationJob.KEY,                    new AvatarMigrationJob.Factory());
      put(BackupJitterMigrationJob.KEY,              new BackupJitterMigrationJob.Factory());
      put(BackupNotificationMigrationJob.KEY,        new BackupNotificationMigrationJob.Factory());
      put(BlobStorageLocationMigrationJob.KEY,       new BlobStorageLocationMigrationJob.Factory());
      put(CachedAttachmentsMigrationJob.KEY,         new CachedAttachmentsMigrationJob.Factory());
      put(ClearGlideCacheMigrationJob.KEY,           new ClearGlideCacheMigrationJob.Factory());
      put(DatabaseMigrationJob.KEY,                  new DatabaseMigrationJob.Factory());
      put(DecryptionsDrainedMigrationJob.KEY,        new DecryptionsDrainedMigrationJob.Factory());
      put(DeleteDeprecatedLogsMigrationJob.KEY,      new DeleteDeprecatedLogsMigrationJob.Factory());
      put(DirectoryRefreshMigrationJob.KEY,          new DirectoryRefreshMigrationJob.Factory());
      put(EmojiDownloadMigrationJob.KEY,             new EmojiDownloadMigrationJob.Factory());
      put(KbsEnclaveMigrationJob.KEY,                new KbsEnclaveMigrationJob.Factory());
      put(LegacyMigrationJob.KEY,                    new LegacyMigrationJob.Factory());
      put(MigrationCompleteJob.KEY,                  new MigrationCompleteJob.Factory());
      put(OptimizeMessageSearchIndexMigrationJob.KEY,new OptimizeMessageSearchIndexMigrationJob.Factory());
      put(PinOptOutMigration.KEY,                    new PinOptOutMigration.Factory());
      put(PinReminderMigrationJob.KEY,               new PinReminderMigrationJob.Factory());
      put(PniAccountInitializationMigrationJob.KEY,  new PniAccountInitializationMigrationJob.Factory());
      put(PniMigrationJob.KEY,                       new PniMigrationJob.Factory());
      put(PreKeysSyncMigrationJob.KEY,               new PreKeysSyncMigrationJob.Factory());
      put(ProfileMigrationJob.KEY,                   new ProfileMigrationJob.Factory());
      put(ProfileSharingUpdateMigrationJob.KEY,      new ProfileSharingUpdateMigrationJob.Factory());
      put(RebuildMessageSearchIndexMigrationJob.KEY, new RebuildMessageSearchIndexMigrationJob.Factory());
      put(RecipientSearchMigrationJob.KEY,           new RecipientSearchMigrationJob.Factory());
      put(RegistrationPinV2MigrationJob.KEY,         new RegistrationPinV2MigrationJob.Factory());
      put(StickerLaunchMigrationJob.KEY,             new StickerLaunchMigrationJob.Factory());
      put(StickerAdditionMigrationJob.KEY,           new StickerAdditionMigrationJob.Factory());
      put(StickerDayByDayMigrationJob.KEY,           new StickerDayByDayMigrationJob.Factory());
      put(StickerMyDailyLifeMigrationJob.KEY,        new StickerMyDailyLifeMigrationJob.Factory());
      put(StorageCapabilityMigrationJob.KEY,         new StorageCapabilityMigrationJob.Factory());
      put(StorageServiceMigrationJob.KEY,            new StorageServiceMigrationJob.Factory());
      put(StorageServiceSystemNameMigrationJob.KEY,  new StorageServiceSystemNameMigrationJob.Factory());
      put(StoryReadStateMigrationJob.KEY,            new StoryReadStateMigrationJob.Factory());
      put(StoryViewedReceiptsStateMigrationJob.KEY,  new StoryViewedReceiptsStateMigrationJob.Factory());
      put(SyncDistributionListsMigrationJob.KEY,     new SyncDistributionListsMigrationJob.Factory());
      put(TrimByLengthSettingsMigrationJob.KEY,      new TrimByLengthSettingsMigrationJob.Factory());
      put(UpdateSmsJobsMigrationJob.KEY,             new UpdateSmsJobsMigrationJob.Factory());
      put(UserNotificationMigrationJob.KEY,          new UserNotificationMigrationJob.Factory());
      put(UuidMigrationJob.KEY,                      new UuidMigrationJob.Factory());

      // Dead jobs
      put(FailingJob.KEY,                            new FailingJob.Factory());
      put(PassingMigrationJob.KEY,                   new PassingMigrationJob.Factory());
      put("PushContentReceiveJob",                   new FailingJob.Factory());
      put("AttachmentUploadJob",                     new FailingJob.Factory());
      put("MmsSendJob",                              new FailingJob.Factory());
      put("RefreshUnidentifiedDeliveryAbilityJob",   new FailingJob.Factory());
      put("Argon2TestJob",                           new FailingJob.Factory());
      put("Argon2TestMigrationJob",                  new PassingMigrationJob.Factory());
      put("StorageKeyRotationMigrationJob",          new PassingMigrationJob.Factory());
      put("StorageSyncJob",                          new StorageSyncJob.Factory());
      put("WakeGroupV2Job",                          new FailingJob.Factory());
      put("LeaveGroupJob",                           new FailingJob.Factory());
      put("PushGroupUpdateJob",                      new FailingJob.Factory());
      put("RequestGroupInfoJob",                     new FailingJob.Factory());
      put("RotateSignedPreKeyJob",                   new PreKeysSyncJob.Factory());
      put("CreateSignedPreKeyJob",                   new PreKeysSyncJob.Factory());
      put("RefreshPreKeysJob",                       new PreKeysSyncJob.Factory());
      put("RecipientChangedNumberJob",               new FailingJob.Factory());
      put("PushTextSendJob",                         new IndividualSendJob.Factory());
      put("MultiDevicePniIdentityUpdateJob",         new FailingJob.Factory());
      put("MultiDeviceGroupUpdateJob",               new FailingJob.Factory());
    }};
  }

  public static Map<String, Constraint.Factory> getConstraintFactories(@NonNull Application application) {
    return new HashMap<String, Constraint.Factory>() {{
      put(AutoDownloadEmojiConstraint.KEY,           new AutoDownloadEmojiConstraint.Factory(application));
      put(ChangeNumberConstraint.KEY,                new ChangeNumberConstraint.Factory());
      put(ChargingConstraint.KEY,                    new ChargingConstraint.Factory());
      put(DecryptionsDrainedConstraint.KEY,          new DecryptionsDrainedConstraint.Factory());
      put(NetworkConstraint.KEY,                     new NetworkConstraint.Factory(application));
      put(NetworkOrCellServiceConstraint.KEY,        new NetworkOrCellServiceConstraint.Factory(application));
      put(NetworkOrCellServiceConstraint.LEGACY_KEY, new NetworkOrCellServiceConstraint.Factory(application));
      put(NotInCallConstraint.KEY,                   new NotInCallConstraint.Factory());
      put(SqlCipherMigrationConstraint.KEY,          new SqlCipherMigrationConstraint.Factory(application));
    }};
  }

  public static List<ConstraintObserver> getConstraintObservers(@NonNull Application application) {
    return Arrays.asList(CellServiceConstraintObserver.getInstance(application),
                         new ChargingConstraintObserver(application),
                         new NetworkConstraintObserver(application),
                         new SqlCipherMigrationConstraintObserver(),
                         new DecryptionsDrainedConstraintObserver(),
                         new NotInCallConstraintObserver());
  }

  public static List<JobMigration> getJobMigrations(@NonNull Application application) {
    return Arrays.asList(new RecipientIdJobMigration(application),
                         new RecipientIdFollowUpJobMigration(),
                         new RecipientIdFollowUpJobMigration2(),
                         new SendReadReceiptsJobMigration(SignalDatabase.messages()),
                         new PushProcessMessageQueueJobMigration(application),
                         new RetrieveProfileJobMigration(),
                         new PushDecryptMessageJobEnvelopeMigration(application),
                         new SenderKeyDistributionSendJobRecipientMigration());
  }
}
