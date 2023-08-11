package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.list

import org.mycrimes.insecuretests.database.model.DonationReceiptRecord

data class DonationReceiptListPageState(
  val records: List<DonationReceiptRecord> = emptyList(),
  val isLoaded: Boolean = false
)
