package org.mycrimes.insecuretests.conversation.v2

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.GroupRecord
import org.mycrimes.insecuretests.recipients.Recipient
import java.util.Optional

class ConversationRecipientRepository(threadId: Long) {

  val conversationRecipient: Observable<Recipient> by lazy {
    val threadRecipientId = Single.fromCallable {
      SignalDatabase.threads.getRecipientIdForThreadId(threadId)!!
    }

    threadRecipientId
      .flatMapObservable { Recipient.observable(it) }
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .replay(1)
      .refCount()
      .observeOn(Schedulers.io())
  }

  val groupRecord: Observable<Optional<GroupRecord>> by lazy {
    conversationRecipient
      .switchMapSingle {
        Single.fromCallable {
          if (it.isGroup) {
            SignalDatabase.groups.getGroup(it.id)
          } else {
            Optional.empty()
          }
        }
      }
      .replay(1)
      .refCount()
      .observeOn(Schedulers.io())
  }
}
