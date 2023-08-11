package org.mycrimes.insecuretests.components.settings.app

import org.mycrimes.insecuretests.recipients.Recipient

data class AppSettingsState(
  val self: Recipient,
  val unreadPaymentsCount: Int,
  val hasExpiredGiftBadge: Boolean,
  val allowUserToGoToDonationManagementScreen: Boolean,
  val userUnregistered: Boolean,
  val clientDeprecated: Boolean
) {
  fun isDeprecatedOrUnregistered(): Boolean {
    return !(userUnregistered || clientDeprecated)
  }
}
