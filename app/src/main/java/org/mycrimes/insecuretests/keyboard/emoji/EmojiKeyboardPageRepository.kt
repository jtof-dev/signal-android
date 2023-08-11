package org.mycrimes.insecuretests.keyboard.emoji

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.mycrimes.insecuretests.components.emoji.EmojiPageModel
import org.mycrimes.insecuretests.components.emoji.RecentEmojiPageModel
import org.mycrimes.insecuretests.emoji.EmojiSource.Companion.latest
import org.mycrimes.insecuretests.util.TextSecurePreferences
import java.util.function.Consumer

class EmojiKeyboardPageRepository(private val context: Context) {
  fun getEmoji(consumer: Consumer<List<EmojiPageModel>>) {
    SignalExecutors.BOUNDED.execute {
      val list = mutableListOf<EmojiPageModel>()
      list += RecentEmojiPageModel(context, TextSecurePreferences.RECENT_STORAGE_KEY)
      list += latest.displayPages
      consumer.accept(list)
    }
  }
}
