package org.mycrimes.insecuretests.stories.settings.create

import org.mycrimes.insecuretests.recipients.RecipientId

data class CreateStoryWithViewersState(
  val label: CharSequence = "",
  val error: NameError? = null,
  val saveState: SaveState = SaveState.Init
) {
  enum class NameError {
    NO_LABEL,
    DUPLICATE_LABEL
  }

  sealed class SaveState {
    object Init : SaveState()
    object Saving : SaveState()
    data class Saved(val recipientId: RecipientId) : SaveState()
  }
}
