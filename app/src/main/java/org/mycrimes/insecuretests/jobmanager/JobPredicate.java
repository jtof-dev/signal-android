package org.mycrimes.insecuretests.jobmanager;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.jobmanager.persistence.JobSpec;

public interface JobPredicate {
  JobPredicate NONE = jobSpec -> true;

  boolean shouldRun(@NonNull JobSpec jobSpec);
}
