package org.mycrimes.insecuretests.components.settings.app.subscription.donate.gateway

import org.mycrimes.insecuretests.badges.models.Badge

data class GatewaySelectorState(
  val loading: Boolean = true,
  val badge: Badge,
  val isGooglePayAvailable: Boolean = false,
  val isPayPalAvailable: Boolean = false,
  val isCreditCardAvailable: Boolean = false
)
