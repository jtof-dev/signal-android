package org.mycrimes.insecuretests.components.settings.app.privacy.pnp

import org.mycrimes.insecuretests.keyvalue.PhoneNumberPrivacyValues

data class PhoneNumberPrivacySettingsState(
  val seeMyPhoneNumber: PhoneNumberPrivacyValues.PhoneNumberSharingMode,
  val findMeByPhoneNumber: PhoneNumberPrivacyValues.PhoneNumberListingMode
)
