package org.mycrimes.insecuretests.scribbles;

import androidx.annotation.NonNull;

import org.signal.imageeditor.core.HiddenEditText;
import org.mycrimes.insecuretests.components.emoji.EmojiUtil;

class RemoveEmojiTextFilter implements HiddenEditText.TextFilter {
  @Override
  public String filter(@NonNull String text) {
    return EmojiUtil.stripEmoji(text);
  }
}
