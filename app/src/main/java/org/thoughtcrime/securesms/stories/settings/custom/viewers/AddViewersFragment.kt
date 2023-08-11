package org.mycrimes.insecuretests.stories.settings.custom.viewers

import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.stories.settings.select.BaseStoryRecipientSelectionFragment

/**
 * Allows user to manage users that can view a story for a given distribution list.
 */
class AddViewersFragment : BaseStoryRecipientSelectionFragment() {
  override val actionButtonLabel: Int = R.string.HideStoryFromFragment__done
  override val distributionListId: DistributionListId
    get() = AddViewersFragmentArgs.fromBundle(requireArguments()).distributionListId
}
