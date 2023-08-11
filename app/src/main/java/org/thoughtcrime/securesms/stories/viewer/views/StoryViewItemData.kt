package org.mycrimes.insecuretests.stories.viewer.views

import org.mycrimes.insecuretests.recipients.Recipient

data class StoryViewItemData(
  val recipient: Recipient,
  val timeViewedInMillis: Long
)
