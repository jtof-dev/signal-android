package org.mycrimes.insecuretests.badges.self.featured

import org.mycrimes.insecuretests.badges.models.Badge

data class SelectFeaturedBadgeState(
  val stage: Stage = Stage.INIT,
  val selectedBadge: Badge? = null,
  val allUnlockedBadges: List<Badge> = listOf()
) {
  enum class Stage {
    INIT,
    READY,
    SAVING
  }
}
