package org.mycrimes.insecuretests.messages;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.JobManager;
import org.mycrimes.insecuretests.jobmanager.JobTracker;
import org.mycrimes.insecuretests.jobs.MarkerJob;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Implementations are responsible for fetching and processing a batch of messages.
 */
public abstract class MessageRetrievalStrategy {

  /**
   * Fetches and processes any pending messages. This method should block until the messages are
   * actually stored and processed -- not just retrieved.
   *
   * @return True if everything was successful up until cancelation, false otherwise.
   */
  @WorkerThread
  abstract boolean execute();

  protected static class QueueFindingJobListener implements JobTracker.JobListener {
    private final Set<String> queues = new HashSet<>();

    @Override
    @AnyThread
    public void onStateChanged(@NonNull Job job, @NonNull JobTracker.JobState jobState) {
      synchronized (queues) {
        queues.add(job.getParameters().getQueue());
      }
    }

    @NonNull Set<String> getQueues() {
      synchronized (queues) {
        return new HashSet<>(queues);
      }
    }
  }
}
