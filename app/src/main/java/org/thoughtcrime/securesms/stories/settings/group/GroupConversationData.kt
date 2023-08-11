package org.mycrimes.insecuretests.stories.settings.group

import org.mycrimes.insecuretests.recipients.RecipientId

/**
 * Minimum data needed to launch ConversationActivity for a given grou
 */
data class GroupConversationData(
  val groupRecipientId: RecipientId,
  val groupThreadId: Long
)
