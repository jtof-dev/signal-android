package org.mycrimes.insecuretests.badges.gifts.viewgift.sent

import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.recipients.Recipient

data class ViewSentGiftState(
  val recipient: Recipient? = null,
  val badge: Badge? = null
)
