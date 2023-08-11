package org.mycrimes.insecuretests.badges.view

import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.recipients.Recipient

data class ViewBadgeState(
  val allBadgesVisibleOnProfile: List<Badge> = listOf(),
  val badgeLoadState: LoadState = LoadState.INIT,
  val selectedBadge: Badge? = null,
  val recipient: Recipient? = null
) {
  enum class LoadState {
    INIT,
    LOADED
  }
}
