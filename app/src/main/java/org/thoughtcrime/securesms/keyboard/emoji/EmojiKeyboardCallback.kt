package org.mycrimes.insecuretests.keyboard.emoji

import org.mycrimes.insecuretests.components.emoji.EmojiEventListener
import org.mycrimes.insecuretests.keyboard.emoji.search.EmojiSearchFragment

interface EmojiKeyboardCallback :
  EmojiEventListener,
  EmojiKeyboardPageFragment.Callback,
  EmojiSearchFragment.Callback
