package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.list

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.DonationReceiptRecord

class DonationReceiptListPageRepository {
  fun getRecords(type: DonationReceiptRecord.Type?): Single<List<DonationReceiptRecord>> {
    return Single.fromCallable {
      SignalDatabase.donationReceipts.getReceipts(type)
    }.subscribeOn(Schedulers.io())
  }
}
