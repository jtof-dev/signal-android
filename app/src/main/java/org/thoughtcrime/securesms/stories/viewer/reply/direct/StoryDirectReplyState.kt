package org.mycrimes.insecuretests.stories.viewer.reply.direct

import org.mycrimes.insecuretests.database.model.MessageRecord
import org.mycrimes.insecuretests.recipients.Recipient

data class StoryDirectReplyState(
  val groupDirectReplyRecipient: Recipient? = null,
  val storyRecord: MessageRecord? = null
)
