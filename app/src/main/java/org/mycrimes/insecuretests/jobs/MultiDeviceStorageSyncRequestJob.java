package org.mycrimes.insecuretests.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.crypto.UnidentifiedAccessUtil;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.JsonJobData;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.impl.NetworkConstraint;
import org.mycrimes.insecuretests.net.NotPushRegisteredException;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

public class MultiDeviceStorageSyncRequestJob extends BaseJob {

  public static final String KEY = "MultiDeviceStorageSyncRequestJob";

  private static final String TAG = Log.tag(MultiDeviceStorageSyncRequestJob.class);

  public MultiDeviceStorageSyncRequestJob() {
    this(new Parameters.Builder()
                       .setQueue("MultiDeviceStorageSyncRequestJob")
                       .setMaxInstancesForFactory(2)
                       .addConstraint(NetworkConstraint.KEY)
                       .setMaxAttempts(10)
                       .build());
  }

  private MultiDeviceStorageSyncRequestJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public @Nullable byte[] serialize() {
    return null;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  protected void onRun() throws Exception {
    if (!Recipient.self().isRegistered()) {
      throw new NotPushRegisteredException();
    }

    if (!TextSecurePreferences.isMultiDevice(context)) {
      Log.i(TAG, "Not multi device, aborting...");
      return;
    }

    SignalServiceMessageSender messageSender = ApplicationDependencies.getSignalServiceMessageSender();

    messageSender.sendSyncMessage(SignalServiceSyncMessage.forFetchLatest(SignalServiceSyncMessage.FetchType.STORAGE_MANIFEST),
                                  UnidentifiedAccessUtil.getAccessForSync(context));
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    if (e instanceof ServerRejectedException) return false;
    return e instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {
    Log.w(TAG, "Did not succeed!");
  }

  public static final class Factory implements Job.Factory<MultiDeviceStorageSyncRequestJob> {
    @Override
    public @NonNull MultiDeviceStorageSyncRequestJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new MultiDeviceStorageSyncRequestJob(parameters);
    }
  }
}
