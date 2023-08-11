package org.mycrimes.insecuretests.components.settings.conversation.sounds

import org.mycrimes.insecuretests.database.RecipientTable
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId

data class SoundsAndNotificationsSettingsState(
  val recipientId: RecipientId = Recipient.UNKNOWN.id,
  val muteUntil: Long = 0L,
  val mentionSetting: RecipientTable.MentionSetting = RecipientTable.MentionSetting.DO_NOT_NOTIFY,
  val hasCustomNotificationSettings: Boolean = false,
  val hasMentionsSupport: Boolean = false,
  val channelConsistencyCheckComplete: Boolean = false
)
