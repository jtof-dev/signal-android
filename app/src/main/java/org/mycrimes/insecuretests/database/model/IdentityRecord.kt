package org.mycrimes.insecuretests.database.model

import org.signal.libsignal.protocol.IdentityKey
import org.mycrimes.insecuretests.database.IdentityTable
import org.mycrimes.insecuretests.recipients.RecipientId

data class IdentityRecord(
  val recipientId: RecipientId,
  val identityKey: IdentityKey,
  val verifiedStatus: IdentityTable.VerifiedStatus,
  @get:JvmName("isFirstUse")
  val firstUse: Boolean,
  val timestamp: Long,
  @get:JvmName("isApprovedNonBlocking")
  val nonblockingApproval: Boolean
)
