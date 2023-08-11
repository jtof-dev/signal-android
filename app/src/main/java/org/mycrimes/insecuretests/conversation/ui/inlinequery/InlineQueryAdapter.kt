package org.mycrimes.insecuretests.conversation.ui.inlinequery

import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.util.adapter.mapping.AnyMappingModel
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter

class InlineQueryAdapter(listener: (AnyMappingModel) -> Unit) : MappingAdapter() {
  init {
    registerFactory(InlineQueryEmojiResult.Model::class.java, { InlineQueryEmojiResult.ViewHolder(it, listener) }, R.layout.inline_query_emoji_result)
  }
}
