package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.detail

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.components.settings.app.subscription.getSubscriptionLevels
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.DonationReceiptRecord
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import java.util.Locale

class DonationReceiptDetailRepository {
  fun getSubscriptionLevelName(subscriptionLevel: Int): Single<String> {
    return Single
      .fromCallable {
        ApplicationDependencies
          .getDonationsService()
          .getDonationsConfiguration(Locale.getDefault())
      }
      .flatMap { it.flattenResult() }
      .map { it.getSubscriptionLevels()[subscriptionLevel] ?: throw Exception("Subscription level $subscriptionLevel not found") }
      .map { it.name }
      .subscribeOn(Schedulers.io())
  }

  fun getDonationReceiptRecord(id: Long): Single<DonationReceiptRecord> {
    return Single.fromCallable<DonationReceiptRecord> {
      SignalDatabase.donationReceipts.getReceipt(id)
    }.subscribeOn(Schedulers.io())
  }
}
