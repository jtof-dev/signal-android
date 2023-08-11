package org.mycrimes.insecuretests.mediasend.v2.text

import androidx.annotation.DrawableRes
import org.mycrimes.insecuretests.R

enum class TextColorStyle(@DrawableRes val icon: Int) {
  /**
   * Transparent background.
   */
  NO_BACKGROUND(R.drawable.ic_text_normal),

  /**
   * White background, textColor foreground.
   */
  NORMAL(R.drawable.ic_text_effect),

  /**
   * textColor background with white foreground.
   */
  INVERT(R.drawable.ic_text_effect);
}
