package org.mycrimes.insecuretests.stories.viewer.views

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.database.DatabaseObserver
import org.mycrimes.insecuretests.database.GroupReceiptTable
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.MessageRecord
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.whispersystems.signalservice.api.push.DistributionId

class StoryViewsRepository {

  companion object {
    private val TAG = Log.tag(StoryViewsRepository::class.java)
  }

  fun isReadReceiptsEnabled(): Boolean = SignalStore.storyValues().viewedReceiptsEnabled

  fun getStoryRecipient(storyId: Long): Single<Recipient> {
    return Single.fromCallable {
      SignalDatabase.messages.getMessageRecord(storyId).toRecipient
    }.subscribeOn(Schedulers.io())
  }

  fun getViews(storyId: Long): Observable<List<StoryViewItemData>> {
    return Observable.create<List<StoryViewItemData>> { emitter ->
      val record: MessageRecord = SignalDatabase.messages.getMessageRecord(storyId)
      val filterIds: Set<RecipientId> = if (record.toRecipient.isDistributionList) {
        val distributionId: DistributionId = SignalDatabase.distributionLists.getDistributionId(record.toRecipient.requireDistributionListId())!!
        SignalDatabase.storySends.getRecipientsForDistributionId(storyId, distributionId)
      } else {
        emptySet()
      }

      fun refresh() {
        emitter.onNext(
          SignalDatabase.groupReceipts.getGroupReceiptInfo(storyId).filter {
            it.status == GroupReceiptTable.STATUS_VIEWED
          }.filter {
            filterIds.isEmpty() || it.recipientId in filterIds
          }.map {
            StoryViewItemData(
              recipient = Recipient.resolved(it.recipientId),
              timeViewedInMillis = it.timestamp
            )
          }
        )
      }

      val observer = DatabaseObserver.MessageObserver { refresh() }

      ApplicationDependencies.getDatabaseObserver().registerMessageUpdateObserver(observer)
      emitter.setCancellable {
        ApplicationDependencies.getDatabaseObserver().unregisterObserver(observer)
      }

      refresh()
    }.subscribeOn(Schedulers.io())
  }

  fun removeUserFromStory(user: Recipient, story: Recipient): Completable {
    return Completable.fromAction {
      val distributionListRecord = SignalDatabase.distributionLists.getList(story.requireDistributionListId())!!
      if (user.id in distributionListRecord.members) {
        SignalDatabase.distributionLists.excludeFromStory(user.id, distributionListRecord)
      } else {
        Log.w(TAG, "User is no longer in the distribution list.")
      }
    }
  }
}
