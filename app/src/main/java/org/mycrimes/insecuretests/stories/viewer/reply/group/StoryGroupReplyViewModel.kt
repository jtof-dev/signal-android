package org.mycrimes.insecuretests.stories.viewer.reply.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.signal.paging.ProxyPagingController
import org.mycrimes.insecuretests.conversation.colors.NameColors
import org.mycrimes.insecuretests.database.model.MessageId
import org.mycrimes.insecuretests.groups.GroupId
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.util.rx.RxStore

class StoryGroupReplyViewModel(storyId: Long, repository: StoryGroupReplyRepository) : ViewModel() {

  private val sessionMemberCache: MutableMap<GroupId, Set<Recipient>> = NameColors.createSessionMembersCache()
  private val store = RxStore(StoryGroupReplyState())
  private val disposables = CompositeDisposable()

  val stateSnapshot: StoryGroupReplyState = store.state
  val state: Flowable<StoryGroupReplyState> = store.stateFlowable

  val pagingController: ProxyPagingController<MessageId> = ProxyPagingController()

  init {
    disposables += repository.getThreadId(storyId).subscribe { threadId ->
      store.update { it.copy(threadId = threadId) }
    }

    disposables += repository.getPagedReplies(storyId)
      .doOnNext { pagingController.set(it.controller) }
      .flatMap { it.data }
      .subscribeBy { data ->
        store.update { state ->
          state.copy(
            replies = data,
            loadState = StoryGroupReplyState.LoadState.READY
          )
        }
      }

    disposables += repository.getNameColorsMap(storyId, sessionMemberCache)
      .subscribeBy { nameColors ->
        store.update { state ->
          state.copy(nameColors = nameColors)
        }
      }
  }

  override fun onCleared() {
    disposables.clear()
    store.dispose()
  }

  class Factory(private val storyId: Long, private val repository: StoryGroupReplyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return modelClass.cast(StoryGroupReplyViewModel(storyId, repository)) as T
    }
  }
}
