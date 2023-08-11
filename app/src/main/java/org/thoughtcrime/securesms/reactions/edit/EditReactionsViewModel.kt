package org.mycrimes.insecuretests.reactions.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.signal.core.util.concurrent.SignalExecutors
import org.mycrimes.insecuretests.components.emoji.EmojiUtil
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.keyvalue.EmojiValues
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.storage.StorageSyncHelper
import org.mycrimes.insecuretests.util.livedata.LiveDataUtil
import org.mycrimes.insecuretests.util.livedata.Store

class EditReactionsViewModel : ViewModel() {

  private val emojiValues: EmojiValues = SignalStore.emojiValues()
  private val store: Store<State> = Store(State(reactions = emojiValues.reactions.map { emojiValues.getPreferredVariation(it) }))

  val reactions: LiveData<List<String>> = LiveDataUtil.mapDistinct(store.stateLiveData, State::reactions)
  val selection: LiveData<Int> = LiveDataUtil.mapDistinct(store.stateLiveData, State::selection)

  fun setSelection(selection: Int) {
    store.update { it.copy(selection = selection) }
  }

  fun onEmojiSelected(emoji: String) {
    store.update { state ->
      if (state.selection != NO_SELECTION && state.selection in state.reactions.indices) {
        if (emoji != EmojiUtil.getCanonicalRepresentation(emoji)) {
          emojiValues.setPreferredVariation(emoji)
        }
        val preferredEmoji: String = emojiValues.getPreferredVariation(emoji)
        val newReactions: List<String> = state.reactions.toMutableList().apply { set(state.selection, preferredEmoji) }
        state.copy(reactions = newReactions)
      } else {
        state
      }
    }
  }

  fun resetToDefaults() {
    store.update { it.copy(reactions = EmojiValues.DEFAULT_REACTIONS_LIST) }
  }

  fun save() {
    emojiValues.reactions = store.state.reactions

    SignalExecutors.BOUNDED.execute {
      SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
      StorageSyncHelper.scheduleSyncForDataChange()
    }
  }

  companion object {
    const val NO_SELECTION: Int = -1
  }

  data class State(val selection: Int = NO_SELECTION, val reactions: List<String>)
}
