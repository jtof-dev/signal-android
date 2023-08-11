package org.mycrimes.insecuretests.logsubmit;

import android.content.Context;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;

public class LogSectionJobs implements LogSection {

  @Override
  public @NonNull String getTitle() {
    return "JOBS";
  }

  @Override
  public @NonNull CharSequence getContent(@NonNull Context context) {
    return ApplicationDependencies.getJobManager().getDebugInfo();
  }
}
