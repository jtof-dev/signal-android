package org.mycrimes.insecuretests.safety

import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.recipients.Recipient

sealed class SafetyNumberBucket {
  data class DistributionListBucket(val distributionListId: DistributionListId, val name: String) : SafetyNumberBucket()
  data class GroupBucket(val recipient: Recipient) : SafetyNumberBucket()
  object ContactsBucket : SafetyNumberBucket()
}
