package org.mycrimes.insecuretests.keyboard.emoji

import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.keyboard.KeyboardPageCategoryIconViewHolder
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import java.util.function.Consumer

class EmojiKeyboardPageCategoriesAdapter(private val onPageSelected: Consumer<String>) : MappingAdapter() {
  init {
    registerFactory(RecentsMappingModel::class.java, LayoutFactory({ v -> KeyboardPageCategoryIconViewHolder(v, onPageSelected) }, R.layout.keyboard_pager_category_icon))
    registerFactory(EmojiCategoryMappingModel::class.java, LayoutFactory({ v -> KeyboardPageCategoryIconViewHolder(v, onPageSelected) }, R.layout.keyboard_pager_category_icon))
  }
}
