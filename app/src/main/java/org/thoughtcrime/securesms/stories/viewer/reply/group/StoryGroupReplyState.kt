package org.mycrimes.insecuretests.stories.viewer.reply.group

import org.mycrimes.insecuretests.conversation.colors.NameColor
import org.mycrimes.insecuretests.recipients.RecipientId

data class StoryGroupReplyState(
  val threadId: Long = 0L,
  val replies: List<ReplyBody> = emptyList(),
  val nameColors: Map<RecipientId, NameColor> = emptyMap(),
  val loadState: LoadState = LoadState.INIT
) {
  val noReplies: Boolean = replies.isEmpty()

  enum class LoadState {
    INIT,
    READY
  }
}
