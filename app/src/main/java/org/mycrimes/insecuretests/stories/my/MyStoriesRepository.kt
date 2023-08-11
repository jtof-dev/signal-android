package org.mycrimes.insecuretests.stories.my

import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.conversation.ConversationMessage
import org.mycrimes.insecuretests.database.GroupReceiptTable
import org.mycrimes.insecuretests.database.RxDatabaseObserver
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.MessageRecord
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.sms.MessageSender

class MyStoriesRepository(context: Context) {

  private val context = context.applicationContext

  fun resend(story: MessageRecord): Completable {
    return Completable.fromAction {
      MessageSender.resend(context, story)
    }.subscribeOn(Schedulers.io())
  }

  fun getMyStories(): Observable<List<MyStoriesState.DistributionSet>> {
    return RxDatabaseObserver
      .conversationList
      .toObservable()
      .map {
        val storiesMap = mutableMapOf<Recipient, List<MessageRecord>>()
        SignalDatabase.messages.getAllOutgoingStories(true, -1).use {
          for (messageRecord in it) {
            val currentList = storiesMap[messageRecord.toRecipient] ?: emptyList()
            storiesMap[messageRecord.toRecipient] = (currentList + messageRecord)
          }
        }

        storiesMap.toSortedMap(MyStoryBiasComparator()).map { (r, m) -> createDistributionSet(r, m) }
      }
  }

  private fun createDistributionSet(recipient: Recipient, messageRecords: List<MessageRecord>): MyStoriesState.DistributionSet {
    return MyStoriesState.DistributionSet(
      label = recipient.getDisplayName(context),
      stories = messageRecords.map { record ->
        MyStoriesState.DistributionStory(
          message = ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(context, record, recipient),
          views = SignalDatabase.groupReceipts.getGroupReceiptInfo(record.id).count { it.status == GroupReceiptTable.STATUS_VIEWED }
        )
      }
    )
  }

  /**
   * Biases "My Story" to the top of the list.
   */
  class MyStoryBiasComparator : Comparator<Recipient> {
    override fun compare(o1: Recipient, o2: Recipient): Int {
      return when {
        o1 == o2 -> 0
        o1.isMyStory -> -1
        o2.isMyStory -> 1
        else -> -1
      }
    }
  }
}