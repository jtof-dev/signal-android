package org.mycrimes.insecuretests.mediasend.v2.text

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.mycrimes.insecuretests.conversation.colors.ChatColors
import org.mycrimes.insecuretests.fonts.TextFont
import org.mycrimes.insecuretests.scribbles.HSVColorSlider
import org.mycrimes.insecuretests.util.FeatureFlags

@Parcelize
data class TextStoryPostCreationState(
  val body: CharSequence = "",
  val textColor: Int = HSVColorSlider.getLastColor(),
  val textColorStyle: TextColorStyle = TextColorStyle.NO_BACKGROUND,
  val textAlignment: TextAlignment = if (FeatureFlags.storiesTextFunctions()) TextAlignment.START else TextAlignment.CENTER,
  val textFont: TextFont = TextFont.REGULAR,
  @IntRange(from = 0, to = 100) val textScale: Int = 50,
  val backgroundColor: ChatColors = TextStoryBackgroundColors.getInitialBackgroundColor(),
  val linkPreviewUri: String? = null
) : Parcelable {

  @ColorInt
  @IgnoredOnParcel
  val textForegroundColor: Int = when (textColorStyle) {
    TextColorStyle.NO_BACKGROUND -> textColor
    TextColorStyle.NORMAL -> textColor
    TextColorStyle.INVERT -> getDefaultColorForLightness(textColor)
  }

  @ColorInt
  @IgnoredOnParcel
  val textBackgroundColor: Int = when (textColorStyle) {
    TextColorStyle.NO_BACKGROUND -> Color.TRANSPARENT
    TextColorStyle.NORMAL -> getDefaultColorForLightness(textColor)
    TextColorStyle.INVERT -> textColor
  }

  private fun getDefaultColorForLightness(textColor: Int): Int {
    val hsl = floatArrayOf(0f, 0f, 0f)
    ColorUtils.colorToHSL(textColor, hsl)

    val lightness = hsl[2]

    return if (lightness >= 0.9f) {
      Color.BLACK
    } else {
      Color.WHITE
    }
  }
}
