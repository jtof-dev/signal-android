package org.mycrimes.insecuretests.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.crypto.UnidentifiedAccessUtil;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.database.StickerTable.StickerPackRecordReader;
import org.mycrimes.insecuretests.database.model.StickerPackRecord;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.JsonJobData;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.impl.NetworkConstraint;
import org.mycrimes.insecuretests.net.NotPushRegisteredException;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.signal.core.util.Hex;
import org.mycrimes.insecuretests.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.messages.multidevice.SignalServiceSyncMessage;
import org.whispersystems.signalservice.api.messages.multidevice.StickerPackOperationMessage;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tells a linked desktop about all installed sticker packs.
 */
public class MultiDeviceStickerPackSyncJob extends BaseJob {

  private static final String TAG = Log.tag(MultiDeviceStickerPackSyncJob.class);

  public static final String KEY = "MultiDeviceStickerPackSyncJob";

  public MultiDeviceStickerPackSyncJob() {
    this(new Parameters.Builder()
                           .setQueue("MultiDeviceStickerPackSyncJob")
                           .addConstraint(NetworkConstraint.KEY)
                           .setLifespan(TimeUnit.DAYS.toMillis(1))
                           .build());
  }

  public MultiDeviceStickerPackSyncJob(@NonNull Parameters parameters) {
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

    List<StickerPackOperationMessage> operations = new LinkedList<>();

    try (StickerPackRecordReader reader = new StickerPackRecordReader(SignalDatabase.stickers().getInstalledStickerPacks())) {
      StickerPackRecord pack;
      while ((pack = reader.getNext()) != null) {
        byte[] packIdBytes  = Hex.fromStringCondensed(pack.getPackId());
        byte[] packKeyBytes = Hex.fromStringCondensed(pack.getPackKey());

        operations.add(new StickerPackOperationMessage(packIdBytes, packKeyBytes, StickerPackOperationMessage.Type.INSTALL));
      }
    }

    SignalServiceMessageSender messageSender = ApplicationDependencies.getSignalServiceMessageSender();
    messageSender.sendSyncMessage(SignalServiceSyncMessage.forStickerPackOperations(operations),
                                  UnidentifiedAccessUtil.getAccessForSync(context));
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    if (e instanceof ServerRejectedException) return false;
    return e instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {
    Log.w(TAG, "Failed to sync sticker pack operation!");
  }

  public static class Factory implements Job.Factory<MultiDeviceStickerPackSyncJob> {

    @Override
    public @NonNull
    MultiDeviceStickerPackSyncJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new MultiDeviceStickerPackSyncJob(parameters);
    }
  }
}
