package org.mycrimes.insecuretests.conversationlist.chatfilter

import org.mycrimes.insecuretests.conversationlist.model.ConversationFilter

data class ConversationFilterRequest(
  val filter: ConversationFilter,
  val source: ConversationFilterSource
)
