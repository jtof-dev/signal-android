package org.mycrimes.insecuretests.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobs.StickerPackDownloadJob;
import org.mycrimes.insecuretests.stickers.BlessedPacks;

/**
 * Installs Day by Day blessed pack.
 */
public class StickerDayByDayMigrationJob extends MigrationJob {

  public static final String KEY = "StickerDayByDayMigrationJob";

  StickerDayByDayMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StickerDayByDayMigrationJob(@NonNull Parameters parameters) {
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
    ApplicationDependencies.getJobManager().add(StickerPackDownloadJob.forInstall(BlessedPacks.DAY_BY_DAY.getPackId(), BlessedPacks.DAY_BY_DAY.getPackKey(), false));
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<StickerDayByDayMigrationJob> {
    @Override
    public @NonNull StickerDayByDayMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new StickerDayByDayMigrationJob(parameters);
    }
  }
}
