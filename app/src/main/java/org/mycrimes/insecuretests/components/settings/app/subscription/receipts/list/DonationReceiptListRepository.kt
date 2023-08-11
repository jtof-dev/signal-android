package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.list

import io.reactivex.rxjava3.core.Single
import org.mycrimes.insecuretests.badges.Badges
import org.mycrimes.insecuretests.components.settings.app.subscription.getBoostBadges
import org.mycrimes.insecuretests.components.settings.app.subscription.getGiftBadges
import org.mycrimes.insecuretests.components.settings.app.subscription.getSubscriptionLevels
import org.mycrimes.insecuretests.database.model.DonationReceiptRecord
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import java.util.Locale

class DonationReceiptListRepository {
  fun getBadges(): Single<List<DonationReceiptBadge>> {
    return Single.fromCallable {
      ApplicationDependencies.getDonationsService()
        .getDonationsConfiguration(Locale.getDefault())
    }.map { response ->
      if (response.result.isPresent) {
        val config = response.result.get()
        val boostBadge = DonationReceiptBadge(DonationReceiptRecord.Type.BOOST, -1, config.getBoostBadges().first())
        val giftBadge = DonationReceiptBadge(DonationReceiptRecord.Type.GIFT, -1, config.getGiftBadges().first())
        val subBadges = config.getSubscriptionLevels().map {
          DonationReceiptBadge(
            level = it.key,
            badge = Badges.fromServiceBadge(it.value.badge),
            type = DonationReceiptRecord.Type.RECURRING
          )
        }
        subBadges + boostBadge + giftBadge
      } else {
        emptyList()
      }
    }
  }
}
