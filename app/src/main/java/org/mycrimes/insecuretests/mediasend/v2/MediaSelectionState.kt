package org.mycrimes.insecuretests.mediasend.v2

import android.net.Uri
import org.mycrimes.insecuretests.conversation.MessageSendType
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.mediasend.Media
import org.mycrimes.insecuretests.mediasend.MediaSendConstants
import org.mycrimes.insecuretests.mms.SentMediaQuality
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.stories.Stories
import org.mycrimes.insecuretests.util.FeatureFlags

data class MediaSelectionState(
  val sendType: MessageSendType,
  val selectedMedia: List<Media> = listOf(),
  val focusedMedia: Media? = null,
  val recipient: Recipient? = null,
  val quality: SentMediaQuality = SignalStore.settings().sentMediaQuality,
  val message: CharSequence? = null,
  val viewOnceToggleState: ViewOnceToggleState = ViewOnceToggleState.INFINITE,
  val isTouchEnabled: Boolean = true,
  val isSent: Boolean = false,
  val isPreUploadEnabled: Boolean = false,
  val isMeteredConnection: Boolean = false,
  val editorStateMap: Map<Uri, Any> = mapOf(),
  val cameraFirstCapture: Media? = null,
  val isStory: Boolean,
  val storySendRequirements: Stories.MediaTransform.SendRequirements = Stories.MediaTransform.SendRequirements.CAN_NOT_SEND,
  val suppressEmptyError: Boolean = true
) {

  val maxSelection = if (sendType.usesSmsTransport) {
    MediaSendConstants.MAX_SMS
  } else {
    FeatureFlags.maxAttachmentCount()
  }

  val canSend = !isSent && selectedMedia.isNotEmpty()

  enum class ViewOnceToggleState(val code: Int) {
    INFINITE(0),
    ONCE(1);

    fun next(): ViewOnceToggleState {
      return when (this) {
        INFINITE -> ONCE
        ONCE -> INFINITE
      }
    }

    companion object {
      fun fromCode(code: Int): ViewOnceToggleState {
        return when (code) {
          1 -> ONCE
          else -> INFINITE
        }
      }
    }
  }
}
