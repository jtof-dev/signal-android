package org.mycrimes.insecuretests.database.model

import org.mycrimes.insecuretests.recipients.RecipientId

class StoryResult(
  val recipientId: RecipientId,
  val messageId: Long,
  val messageSentTimestamp: Long,
  val isOutgoing: Boolean
)
