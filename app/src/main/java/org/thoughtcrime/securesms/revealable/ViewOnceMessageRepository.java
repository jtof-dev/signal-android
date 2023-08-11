package org.mycrimes.insecuretests.revealable;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.database.MessageTable;
import org.mycrimes.insecuretests.database.NoSuchMessageException;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.database.model.MmsMessageRecord;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.MultiDeviceViewedUpdateJob;
import org.mycrimes.insecuretests.jobs.SendViewedReceiptJob;

import java.util.Collections;
import java.util.Optional;

class ViewOnceMessageRepository {

  private static final String TAG = Log.tag(ViewOnceMessageRepository.class);

  private final MessageTable mmsDatabase;

  ViewOnceMessageRepository(@NonNull Context context) {
    this.mmsDatabase = SignalDatabase.messages();
  }

  void getMessage(long messageId, @NonNull Callback<Optional<MmsMessageRecord>> callback) {
    SignalExecutors.BOUNDED.execute(() -> {
      try {
        MmsMessageRecord record = (MmsMessageRecord) mmsDatabase.getMessageRecord(messageId);

        MessageTable.MarkedMessageInfo info = mmsDatabase.setIncomingMessageViewed(record.getId());
        if (info != null) {
          ApplicationDependencies.getJobManager().add(new SendViewedReceiptJob(record.getThreadId(),
                                                                               info.getSyncMessageId().getRecipientId(),
                                                                               info.getSyncMessageId().getTimetamp(),
                                                                               info.getMessageId()));
          MultiDeviceViewedUpdateJob.enqueue(Collections.singletonList(info.getSyncMessageId()));
        }

        callback.onComplete(Optional.ofNullable(record));
      } catch (NoSuchMessageException e) {
        callback.onComplete(Optional.empty());
      }
    });
  }

  interface Callback<T> {
    void onComplete(T result);
  }
}
