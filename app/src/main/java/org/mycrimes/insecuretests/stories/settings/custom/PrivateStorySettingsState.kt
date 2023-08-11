package org.mycrimes.insecuretests.stories.settings.custom

import org.mycrimes.insecuretests.database.model.DistributionListRecord

data class PrivateStorySettingsState(
  val privateStory: DistributionListRecord? = null,
  val areRepliesAndReactionsEnabled: Boolean = false,
  val isActionInProgress: Boolean = false
)
