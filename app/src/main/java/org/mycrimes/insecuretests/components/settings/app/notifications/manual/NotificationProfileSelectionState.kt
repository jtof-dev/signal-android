package org.mycrimes.insecuretests.components.settings.app.notifications.manual

import org.mycrimes.insecuretests.notifications.profiles.NotificationProfile
import java.time.LocalDateTime

data class NotificationProfileSelectionState(
  val notificationProfiles: List<NotificationProfile> = listOf(),
  val expandedId: Long = -1L,
  val timeSlotB: LocalDateTime
)
