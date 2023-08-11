package org.mycrimes.insecuretests.keyboard.emoji

import android.content.Context
import android.graphics.drawable.Drawable
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.emoji.RecentEmojiPageModel
import org.mycrimes.insecuretests.emoji.EmojiCategory
import org.mycrimes.insecuretests.keyboard.KeyboardPageCategoryIconMappingModel
import org.mycrimes.insecuretests.util.ThemeUtil

class RecentsMappingModel(override val selected: Boolean) : KeyboardPageCategoryIconMappingModel<RecentsMappingModel> {
  override val key: String = RecentEmojiPageModel.KEY

  override fun getIcon(context: Context): Drawable {
    return requireNotNull(ThemeUtil.getThemedDrawable(context, R.attr.emoji_category_recent))
  }

  override fun areItemsTheSame(newItem: RecentsMappingModel): Boolean {
    return newItem.key == key
  }

  override fun areContentsTheSame(newItem: RecentsMappingModel): Boolean {
    return areItemsTheSame(newItem) && selected == newItem.selected
  }
}

class EmojiCategoryMappingModel(private val emojiCategory: EmojiCategory, override val selected: Boolean) : KeyboardPageCategoryIconMappingModel<EmojiCategoryMappingModel> {
  override val key: String = emojiCategory.key

  override fun getIcon(context: Context): Drawable {
    return requireNotNull(ThemeUtil.getThemedDrawable(context, emojiCategory.icon))
  }

  override fun areItemsTheSame(newItem: EmojiCategoryMappingModel): Boolean {
    return newItem.key == key
  }

  override fun areContentsTheSame(newItem: EmojiCategoryMappingModel): Boolean {
    return areItemsTheSame(newItem) &&
      selected == newItem.selected &&
      newItem.emojiCategory == emojiCategory
  }
}
