package org.mycrimes.insecuretests.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobs.StickerPackDownloadJob;
import org.mycrimes.insecuretests.stickers.BlessedPacks;

/**
 * Installs Piece of Cake blessed pack.
 */
public class StickerMyDailyLifeMigrationJob extends MigrationJob {

  public static final String KEY = "StickerMyDailyLifeMigrationJob";

  StickerMyDailyLifeMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private StickerMyDailyLifeMigrationJob(@NonNull Parameters parameters) {
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
    ApplicationDependencies.getJobManager().add(StickerPackDownloadJob.forInstall(BlessedPacks.MY_DAILY_LIFE.getPackId(), BlessedPacks.MY_DAILY_LIFE.getPackKey(), false));
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<StickerMyDailyLifeMigrationJob> {
    @Override
    public @NonNull StickerMyDailyLifeMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new StickerMyDailyLifeMigrationJob(parameters);
    }
  }
}
