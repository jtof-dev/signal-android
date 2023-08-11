package org.mycrimes.insecuretests.stories.settings.select

import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.database.model.DistributionListRecord
import org.mycrimes.insecuretests.recipients.RecipientId

data class BaseStoryRecipientSelectionState(
  val distributionListId: DistributionListId?,
  val privateStory: DistributionListRecord? = null,
  val selection: Set<RecipientId> = emptySet()
)
