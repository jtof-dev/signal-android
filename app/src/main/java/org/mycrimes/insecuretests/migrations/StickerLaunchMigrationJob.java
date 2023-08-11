package org.mycrimes.insecuretests.migrations;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.database.StickerTable;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.JobManager;
import org.mycrimes.insecuretests.jobs.MultiDeviceStickerPackOperationJob;
import org.mycrimes.insecuretests.jobs.StickerPackDownloadJob;
import org.mycrimes.insecuretests.stickers.BlessedPacks;
import org.mycrimes.insecuretests.util.TextSecurePreferences;

public class StickerLaunchMigrationJob extends MigrationJob {

  public static final String KEY = "StickerLaunchMigrationJob";

  StickerLaunchMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StickerLaunchMigrationJob(@NonNull Parameters parameters) {
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
    installPack(context, BlessedPacks.ZOZO);
    installPack(context, BlessedPacks.BANDIT);
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  private static void installPack(@NonNull Context context, @NonNull BlessedPacks.Pack pack) {
    JobManager   jobManager      = ApplicationDependencies.getJobManager();
    StickerTable stickerDatabase = SignalDatabase.stickers();

    if (stickerDatabase.isPackAvailableAsReference(pack.getPackId())) {
      stickerDatabase.markPackAsInstalled(pack.getPackId(), false);
    }

    jobManager.add(StickerPackDownloadJob.forInstall(pack.getPackId(), pack.getPackKey(), false));

    if (TextSecurePreferences.isMultiDevice(context)) {
      jobManager.add(new MultiDeviceStickerPackOperationJob(pack.getPackId(), pack.getPackKey(), MultiDeviceStickerPackOperationJob.Type.INSTALL));
    }
  }

  public static class Factory implements Job.Factory<StickerLaunchMigrationJob> {
    @Override
    public @NonNull
    StickerLaunchMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new StickerLaunchMigrationJob(parameters);
    }
  }
}
