package org.mycrimes.insecuretests.database.model

import org.mycrimes.insecuretests.recipients.RecipientId

/** A model for [org.mycrimes.insecuretests.database.PendingRetryReceiptTable] */
data class PendingRetryReceiptModel(
  val id: Long,
  val author: RecipientId,
  val authorDevice: Int,
  val sentTimestamp: Long,
  val receivedTimestamp: Long,
  val threadId: Long
)
