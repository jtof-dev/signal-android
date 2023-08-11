package org.mycrimes.insecuretests.stories.settings.my

import androidx.annotation.WorkerThread
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.database.model.DistributionListPrivacyData
import org.mycrimes.insecuretests.database.model.DistributionListPrivacyMode
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.stories.Stories
import org.mycrimes.insecuretests.stories.settings.privacy.ChooseInitialMyStoryMembershipState

class MyStorySettingsRepository {

  fun getPrivacyState(): Single<MyStoryPrivacyState> {
    return Single.fromCallable {
      getStoryPrivacyState()
    }.subscribeOn(Schedulers.io())
  }

  fun observeChooseInitialPrivacy(): Observable<ChooseInitialMyStoryMembershipState> {
    return Single
      .fromCallable { SignalDatabase.distributionLists.getRecipientId(DistributionListId.MY_STORY)!! }
      .subscribeOn(Schedulers.io())
      .flatMapObservable { recipientId ->
        val allSignalConnectionsCount = getAllSignalConnectionsCount().toObservable()
        val stateWithoutCount = Recipient.observable(recipientId)
          .flatMap { Observable.just(ChooseInitialMyStoryMembershipState(recipientId = recipientId, privacyState = getStoryPrivacyState())) }

        Observable.combineLatest(allSignalConnectionsCount, stateWithoutCount) { count, state -> state.copy(allSignalConnectionsCount = count) }
      }
  }

  fun setPrivacyMode(privacyMode: DistributionListPrivacyMode): Completable {
    return Completable.fromAction {
      SignalDatabase.distributionLists.setPrivacyMode(DistributionListId.MY_STORY, privacyMode)
      Stories.onStorySettingsChanged(DistributionListId.MY_STORY)
    }.subscribeOn(Schedulers.io())
  }

  fun getRepliesAndReactionsEnabled(): Single<Boolean> {
    return Single.fromCallable {
      SignalDatabase.distributionLists.getStoryType(DistributionListId.MY_STORY).isStoryWithReplies
    }.subscribeOn(Schedulers.io())
  }

  fun setRepliesAndReactionsEnabled(repliesAndReactionsEnabled: Boolean): Completable {
    return Completable.fromAction {
      SignalDatabase.distributionLists.setAllowsReplies(DistributionListId.MY_STORY, repliesAndReactionsEnabled)
      Stories.onStorySettingsChanged(DistributionListId.MY_STORY)
    }.subscribeOn(Schedulers.io())
  }

  fun getAllSignalConnectionsCount(): Single<Int> {
    return Single.fromCallable {
      SignalDatabase.recipients.getSignalContactsCount(false)
    }.subscribeOn(Schedulers.io())
  }

  @WorkerThread
  private fun getStoryPrivacyState(): MyStoryPrivacyState {
    val privacyData: DistributionListPrivacyData = SignalDatabase.distributionLists.getPrivacyData(DistributionListId.MY_STORY)

    return MyStoryPrivacyState(
      privacyMode = privacyData.privacyMode,
      connectionCount = privacyData.memberCount
    )
  }
}
