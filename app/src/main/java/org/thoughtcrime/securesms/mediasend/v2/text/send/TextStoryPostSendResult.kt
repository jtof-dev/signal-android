package org.mycrimes.insecuretests.mediasend.v2.text.send

import org.mycrimes.insecuretests.database.model.IdentityRecord

sealed class TextStoryPostSendResult {
  object Success : TextStoryPostSendResult()
  object Failure : TextStoryPostSendResult()
  data class UntrustedRecordsError(val untrustedRecords: List<IdentityRecord>) : TextStoryPostSendResult()
}
