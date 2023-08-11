package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.list

import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.database.model.DonationReceiptRecord

data class DonationReceiptBadge(
  val type: DonationReceiptRecord.Type,
  val level: Int,
  val badge: Badge
)
