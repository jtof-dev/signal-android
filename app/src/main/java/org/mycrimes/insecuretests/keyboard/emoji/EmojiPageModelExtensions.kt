package org.mycrimes.insecuretests.keyboard.emoji

import org.mycrimes.insecuretests.components.emoji.EmojiPageModel
import org.mycrimes.insecuretests.components.emoji.EmojiPageViewGridAdapter
import org.mycrimes.insecuretests.components.emoji.RecentEmojiPageModel
import org.mycrimes.insecuretests.components.emoji.parsing.EmojiTree
import org.mycrimes.insecuretests.emoji.EmojiCategory
import org.mycrimes.insecuretests.emoji.EmojiSource
import org.mycrimes.insecuretests.util.adapter.mapping.MappingModel

fun EmojiPageModel.toMappingModels(): List<MappingModel<*>> {
  val emojiTree: EmojiTree = EmojiSource.latest.emojiTree

  return displayEmoji.map {
    val isTextEmoji = EmojiCategory.EMOTICONS.key == key || (RecentEmojiPageModel.KEY == key && emojiTree.getEmoji(it.value, 0, it.value.length) == null)

    if (isTextEmoji) {
      EmojiPageViewGridAdapter.EmojiTextModel(key, it)
    } else {
      EmojiPageViewGridAdapter.EmojiModel(key, it)
    }
  }
}
