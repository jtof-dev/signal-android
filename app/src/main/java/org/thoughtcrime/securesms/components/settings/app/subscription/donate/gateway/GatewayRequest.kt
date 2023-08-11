package org.mycrimes.insecuretests.components.settings.app.subscription.donate.gateway

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.signal.core.util.money.FiatMoney
import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.components.settings.app.subscription.donate.DonateToSignalType
import org.mycrimes.insecuretests.recipients.RecipientId
import java.math.BigDecimal
import java.util.Currency

@Parcelize
data class GatewayRequest(
  val donateToSignalType: DonateToSignalType,
  val badge: Badge,
  val label: String,
  val price: BigDecimal,
  val currencyCode: String,
  val level: Long,
  val recipientId: RecipientId,
  val additionalMessage: String? = null
) : Parcelable {
  @IgnoredOnParcel
  val fiat: FiatMoney = FiatMoney(price, Currency.getInstance(currencyCode))
}
