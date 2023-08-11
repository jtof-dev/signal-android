package org.mycrimes.insecuretests.database.model

import org.mycrimes.insecuretests.recipients.RecipientId

data class DistributionListPartialRecord(
  val id: DistributionListId,
  val name: CharSequence,
  val recipientId: RecipientId,
  val allowsReplies: Boolean,
  val isUnknown: Boolean,
  val privacyMode: DistributionListPrivacyMode
)
