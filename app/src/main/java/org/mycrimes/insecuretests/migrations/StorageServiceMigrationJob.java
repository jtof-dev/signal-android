package org.mycrimes.insecuretests.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.JobManager;
import org.mycrimes.insecuretests.jobs.MultiDeviceKeysUpdateJob;
import org.mycrimes.insecuretests.jobs.StorageSyncJob;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.util.TextSecurePreferences;

/**
 * Just runs a storage sync. Useful if you've started syncing a new field to storage service.
 */
public class StorageServiceMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(StorageServiceMigrationJob.class);

  public static final String KEY = "StorageServiceMigrationJob";

  StorageServiceMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StorageServiceMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public boolean isUiBlocking() {
    return false;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void performMigration() {
    if (SignalStore.account().getAci() == null) {
      Log.w(TAG, "Self not yet available.");
      return;
    }

    SignalDatabase.recipients().markNeedsSync(Recipient.self().getId());

    JobManager jobManager = ApplicationDependencies.getJobManager();

    if (TextSecurePreferences.isMultiDevice(context)) {
      Log.i(TAG, "Multi-device.");
      jobManager.startChain(new StorageSyncJob())
                .then(new MultiDeviceKeysUpdateJob())
                .enqueue();
    } else {
      Log.i(TAG, "Single-device.");
      jobManager.add(new StorageSyncJob());
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<StorageServiceMigrationJob> {
    @Override
    public @NonNull StorageServiceMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new StorageServiceMigrationJob(parameters);
    }
  }
}
