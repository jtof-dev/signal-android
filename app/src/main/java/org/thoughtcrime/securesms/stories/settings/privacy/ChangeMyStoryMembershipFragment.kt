package org.mycrimes.insecuretests.stories.settings.privacy

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.WrapperDialogFragment
import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.stories.settings.select.BaseStoryRecipientSelectionFragment

abstract class ChangeMyStoryMembershipFragment : BaseStoryRecipientSelectionFragment() {
  override val actionButtonLabel: Int = R.string.HideStoryFromFragment__done

  override val distributionListId: DistributionListId
    get() = DistributionListId.from(DistributionListId.MY_STORY_ID)

  override fun presentTitle(toolbar: Toolbar, size: Int) = Unit
}

/**
 * Allows user to select a list of people to exclude from "My Story"
 */
class AllExceptFragment : ChangeMyStoryMembershipFragment() {
  override val toolbarTitleId: Int = R.string.ChangeMyStoryMembershipFragment__all_except
  override val checkboxResource: Int = R.drawable.contact_selection_exclude_checkbox

  class Dialog : WrapperDialogFragment() {
    override fun getWrappedFragment(): Fragment {
      return AllExceptFragment()
    }
  }

  companion object {
    fun createAsDialog(): DialogFragment {
      return Dialog()
    }
  }
}

/**
 * Allows user to select a list of people to include for "My Story"
 */
class OnlyShareWithFragment : ChangeMyStoryMembershipFragment() {
  override val toolbarTitleId: Int = R.string.ChangeMyStoryMembershipFragment__only_share_with

  class Dialog : WrapperDialogFragment() {
    override fun getWrappedFragment(): Fragment {
      return OnlyShareWithFragment()
    }
  }

  companion object {
    fun createAsDialog(): DialogFragment {
      return Dialog()
    }
  }
}
