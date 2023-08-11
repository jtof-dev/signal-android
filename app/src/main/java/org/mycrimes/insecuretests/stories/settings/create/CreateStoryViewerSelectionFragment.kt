package org.mycrimes.insecuretests.stories.settings.create

import androidx.navigation.fragment.findNavController
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.stories.settings.select.BaseStoryRecipientSelectionFragment
import org.mycrimes.insecuretests.util.navigation.safeNavigate

/**
 * Allows user to select who will see the story they are creating
 */
class CreateStoryViewerSelectionFragment : BaseStoryRecipientSelectionFragment() {
  override val actionButtonLabel: Int = R.string.CreateStoryViewerSelectionFragment__next
  override val distributionListId: DistributionListId? = null

  override fun goToNextScreen(recipients: Set<RecipientId>) {
    findNavController().safeNavigate(CreateStoryViewerSelectionFragmentDirections.actionCreateStoryViewerSelectionToCreateStoryWithViewers(recipients.toTypedArray()))
  }
}
