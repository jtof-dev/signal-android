package org.mycrimes.insecuretests.conversation.mutiselect

import android.view.View
import org.mycrimes.insecuretests.conversation.ConversationMessage
import org.mycrimes.insecuretests.conversation.colors.Colorizable
import org.mycrimes.insecuretests.giph.mp4.GiphyMp4Playable

interface Multiselectable : Colorizable, GiphyMp4Playable {
  val conversationMessage: ConversationMessage

  fun getTopBoundaryOfMultiselectPart(multiselectPart: MultiselectPart): Int

  fun getBottomBoundaryOfMultiselectPart(multiselectPart: MultiselectPart): Int

  fun getMultiselectPartForLatestTouch(): MultiselectPart

  fun getHorizontalTranslationTarget(): View?

  fun hasNonSelectableMedia(): Boolean
}
