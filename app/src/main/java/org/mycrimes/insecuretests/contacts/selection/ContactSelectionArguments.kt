package org.mycrimes.insecuretests.contacts.selection

import android.os.Bundle
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.contacts.ContactSelectionDisplayMode
import org.mycrimes.insecuretests.groups.SelectionLimits
import org.mycrimes.insecuretests.recipients.RecipientId

data class ContactSelectionArguments(
  val displayMode: Int = ContactSelectionDisplayMode.FLAG_ALL,
  val isRefreshable: Boolean = true,
  val displayRecents: Boolean = false,
  val selectionLimits: SelectionLimits? = null,
  val currentSelection: List<RecipientId> = emptyList(),
  val displaySelectionCount: Boolean = true,
  val canSelectSelf: Boolean = selectionLimits == null,
  val displayChips: Boolean = true,
  val recyclerPadBottom: Int = -1,
  val recyclerChildClipping: Boolean = true,
  val checkboxResource: Int = R.drawable.contact_selection_checkbox
) {

  fun toArgumentBundle(): Bundle {
    return Bundle().apply {
      putInt(DISPLAY_MODE, displayMode)
      putBoolean(REFRESHABLE, isRefreshable)
      putBoolean(RECENTS, displayRecents)
      putParcelable(SELECTION_LIMITS, selectionLimits)
      putBoolean(HIDE_COUNT, !displaySelectionCount)
      putBoolean(CAN_SELECT_SELF, canSelectSelf)
      putBoolean(DISPLAY_CHIPS, displayChips)
      putInt(RV_PADDING_BOTTOM, recyclerPadBottom)
      putBoolean(RV_CLIP, recyclerChildClipping)
      putInt(CHECKBOX_RESOURCE, checkboxResource)
      putParcelableArrayList(CURRENT_SELECTION, ArrayList(currentSelection))
    }
  }

  companion object {
    const val DISPLAY_MODE = "display_mode"
    const val REFRESHABLE = "refreshable"
    const val RECENTS = "recents"
    const val SELECTION_LIMITS = "selection_limits"
    const val CURRENT_SELECTION = "current_selection"
    const val HIDE_COUNT = "hide_count"
    const val CAN_SELECT_SELF = "can_select_self"
    const val DISPLAY_CHIPS = "display_chips"
    const val RV_PADDING_BOTTOM = "recycler_view_padding_bottom"
    const val RV_CLIP = "recycler_view_clipping"
    const val CHECKBOX_RESOURCE = "checkbox_resource"
  }
}