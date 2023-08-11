package org.mycrimes.insecuretests.insights;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.annimon.stream.Stream;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.contacts.avatars.GeneratedContactPhoto;
import org.mycrimes.insecuretests.contacts.avatars.ProfileContactPhoto;
import org.mycrimes.insecuretests.database.MessageTable;
import org.mycrimes.insecuretests.database.RecipientTable;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.mms.OutgoingMessage;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.recipients.RecipientId;
import org.mycrimes.insecuretests.sms.MessageSender;
import org.mycrimes.insecuretests.util.Util;
import org.signal.core.util.concurrent.SimpleTask;

import java.util.List;
import java.util.Optional;

public class InsightsRepository implements InsightsDashboardViewModel.Repository, InsightsModalViewModel.Repository {

  private final Context context;

  public InsightsRepository(Context context) {
    this.context = context.getApplicationContext();
  }

  @Override
  public void getInsightsData(@NonNull Consumer<InsightsData> insightsDataConsumer) {
    SimpleTask.run(() -> {
      MessageTable messageTable = SignalDatabase.messages();
      int          insecure     = messageTable.getInsecureMessageCountForInsights();
      int          secure       = messageTable.getSecureMessageCountForInsights();

      if (insecure + secure == 0) {
        return new InsightsData(false, 0);
      } else {
        return new InsightsData(true, Util.clamp((int) Math.ceil((insecure * 100f) / (insecure + secure)), 0, 100));
      }
    }, insightsDataConsumer::accept);
  }

  @Override
  public void getInsecureRecipients(@NonNull Consumer<List<Recipient>> insecureRecipientsConsumer) {
    SimpleTask.run(() -> {
      RecipientTable    recipientTable         = SignalDatabase.recipients();
      List<RecipientId> unregisteredRecipients = recipientTable.getUninvitedRecipientsForInsights();

      return Stream.of(unregisteredRecipients)
                   .map(Recipient::resolved)
                   .toList();
    },
    insecureRecipientsConsumer::accept);
  }

  @Override
  public void getUserAvatar(@NonNull Consumer<InsightsUserAvatar> avatarConsumer) {
    SimpleTask.run(() -> {
      Recipient self = Recipient.self().resolve();
      String    name = Optional.of(self.getDisplayName(context)).orElse("");

      return new InsightsUserAvatar(new ProfileContactPhoto(self),
                                    self.getAvatarColor(),
                                    new GeneratedContactPhoto(name, R.drawable.ic_profile_outline_40));
    }, avatarConsumer::accept);
  }

  @Override
  public void sendSmsInvite(@NonNull Recipient recipient, Runnable onSmsMessageSent) {
    SimpleTask.run(() -> {
      Recipient resolved       = recipient.resolve();
      int       subscriptionId = resolved.getDefaultSubscriptionId().orElse(-1);
      String    message        = context.getString(R.string.InviteActivity_lets_switch_to_signal, context.getString(R.string.install_url));

      MessageSender.send(context, OutgoingMessage.sms(resolved, message, subscriptionId), -1L, MessageSender.SendType.SMS, null, null);

      RecipientTable database = SignalDatabase.recipients();
      database.setHasSentInvite(recipient.getId());

      return null;
    }, v -> onSmsMessageSent.run());
  }
}
