package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.detail

import org.mycrimes.insecuretests.database.model.DonationReceiptRecord

data class DonationReceiptDetailState(
  val donationReceiptRecord: DonationReceiptRecord? = null,
  val subscriptionName: String? = null
)
