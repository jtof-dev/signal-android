package org.mycrimes.insecuretests.components.settings.app.internal.donor

import org.signal.donations.StripeDeclineCode
import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.components.settings.app.subscription.errors.UnexpectedSubscriptionCancellation

data class InternalDonorErrorConfigurationState(
  val badges: List<Badge> = emptyList(),
  val selectedBadge: Badge? = null,
  val selectedUnexpectedSubscriptionCancellation: UnexpectedSubscriptionCancellation? = null,
  val selectedStripeDeclineCode: StripeDeclineCode.Code? = null
)
