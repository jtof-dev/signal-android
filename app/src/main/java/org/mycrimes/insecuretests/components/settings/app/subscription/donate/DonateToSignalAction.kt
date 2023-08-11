package org.mycrimes.insecuretests.components.settings.app.subscription.donate

import org.mycrimes.insecuretests.components.settings.app.subscription.donate.gateway.GatewayRequest

sealed class DonateToSignalAction {
  data class DisplayCurrencySelectionDialog(val donateToSignalType: DonateToSignalType, val supportedCurrencies: List<String>) : DonateToSignalAction()
  data class DisplayGatewaySelectorDialog(val gatewayRequest: GatewayRequest) : DonateToSignalAction()
  data class CancelSubscription(val gatewayRequest: GatewayRequest) : DonateToSignalAction()
  data class UpdateSubscription(val gatewayRequest: GatewayRequest) : DonateToSignalAction()
}
