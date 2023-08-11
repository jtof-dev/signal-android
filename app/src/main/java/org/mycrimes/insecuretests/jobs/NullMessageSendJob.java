package org.mycrimes.insecuretests.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.crypto.UnidentifiedAccessUtil;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.JsonJobData;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.impl.NetworkConstraint;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.recipients.RecipientId;
import org.mycrimes.insecuretests.recipients.RecipientUtil;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccessPair;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Just sends an empty message to a target recipient. Only suitable for individuals, NOT groups.
 */
public class NullMessageSendJob extends BaseJob {

  public static final String KEY = "NullMessageSendJob";

  private static final String TAG = Log.tag(NullMessageSendJob.class);

  private final RecipientId recipientId;

  private static final String KEY_RECIPIENT_ID = "recipient_id";

  public NullMessageSendJob(@NonNull RecipientId recipientId) {
    this(recipientId,
         new Parameters.Builder()
                       .setQueue(recipientId.toQueueKey())
                       .addConstraint(NetworkConstraint.KEY)
                       .setLifespan(TimeUnit.DAYS.toMillis(1))
                       .setMaxAttempts(Parameters.UNLIMITED)
                       .build());
  }

  private NullMessageSendJob(@NonNull RecipientId recipientId, @NonNull Parameters parameters) {
    super(parameters);
    this.recipientId = recipientId;
  }

  @Override
  public @Nullable byte[] serialize() {
    return new JsonJobData.Builder().putString(KEY_RECIPIENT_ID, recipientId.serialize()).serialize();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  protected void onRun() throws Exception {
    Recipient recipient = Recipient.resolved(recipientId);

    if (recipient.isGroup()) {
      Log.w(TAG, "Groups are not supported!");
      return;
    }

    if (recipient.isUnregistered()) {
      Log.w(TAG, recipient.getId() + " not registered!");
    }

    SignalServiceMessageSender       messageSender      = ApplicationDependencies.getSignalServiceMessageSender();
    SignalServiceAddress             address            = RecipientUtil.toSignalServiceAddress(context, recipient);
    Optional<UnidentifiedAccessPair> unidentifiedAccess = UnidentifiedAccessUtil.getAccessFor(context, recipient);

    try {
      messageSender.sendNullMessage(address, unidentifiedAccess);
    } catch (UntrustedIdentityException e) {
      Log.w(TAG, "Unable to send null message.");
    }
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    return e instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {
  }

  public static final class Factory implements Job.Factory<NullMessageSendJob> {

    @Override
    public @NonNull NullMessageSendJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      JsonJobData data = JsonJobData.deserialize(serializedData);

      return new NullMessageSendJob(RecipientId.from(data.getString(KEY_RECIPIENT_ID)),
                                    parameters);
    }
  }
}
