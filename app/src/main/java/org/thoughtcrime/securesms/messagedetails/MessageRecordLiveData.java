package org.mycrimes.insecuretests.messagedetails;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import org.signal.core.util.concurrent.SignalExecutors;
import org.mycrimes.insecuretests.database.DatabaseObserver;
import org.mycrimes.insecuretests.database.MessageTable;
import org.mycrimes.insecuretests.database.NoSuchMessageException;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.database.model.MessageId;
import org.mycrimes.insecuretests.database.model.MessageRecord;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;

final class MessageRecordLiveData extends LiveData<MessageRecord> {

  private final DatabaseObserver.Observer observer;
  private final MessageId                 messageId;

  MessageRecordLiveData(MessageId messageId) {
    this.messageId = messageId;
    this.observer  = this::retrieveMessageRecordActual;
  }

  @Override
  protected void onActive() {
    SignalExecutors.BOUNDED_IO.execute(this::retrieveMessageRecordActual);
  }

  @Override
  protected void onInactive() {
    ApplicationDependencies.getDatabaseObserver().unregisterObserver(observer);
  }

  @WorkerThread
  private synchronized void retrieveMessageRecordActual() {
    try {
      MessageRecord record = SignalDatabase.messages().getMessageRecord(messageId.getId());

      if (record.isPaymentNotification()) {
        record = SignalDatabase.payments().updateMessageWithPayment(record);
      }

      postValue(record);
      ApplicationDependencies.getDatabaseObserver().registerVerboseConversationObserver(record.getThreadId(), observer);
    } catch (NoSuchMessageException ignored) {
      postValue(null);
    }
  }
}
