package org.mycrimes.insecuretests.longmessage

import android.content.Context
import android.net.Uri
import org.signal.core.util.StreamUtil
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.conversation.ConversationMessage
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.MmsMessageRecord
import org.mycrimes.insecuretests.mms.PartAuthority
import org.mycrimes.insecuretests.recipients.Recipient
import java.io.IOException

const val TAG = "LongMessageResolver"

fun readFullBody(context: Context, uri: Uri): String {
  try {
    PartAuthority.getAttachmentStream(context, uri).use { stream -> return StreamUtil.readFullyAsString(stream) }
  } catch (e: IOException) {
    Log.w(TAG, "Failed to read full text body.", e)
    return ""
  }
}

fun MmsMessageRecord.resolveBody(context: Context): ConversationMessage {
  val threadRecipient: Recipient = requireNotNull(SignalDatabase.threads.getRecipientForThreadId(threadId))
  val textSlide = slideDeck.textSlide
  val textSlideUri = textSlide?.uri
  return if (textSlide != null && textSlideUri != null) {
    ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(context, this, readFullBody(context, textSlideUri), threadRecipient)
  } else {
    ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(context, this, threadRecipient)
  }
}
