package org.mycrimes.insecuretests.stories.settings.group

import org.mycrimes.insecuretests.recipients.Recipient

data class GroupStorySettingsState(
  val name: String = "",
  val members: List<Recipient> = emptyList(),
  val removed: Boolean = false
)
