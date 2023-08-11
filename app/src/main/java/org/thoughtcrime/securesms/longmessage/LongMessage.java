package org.mycrimes.insecuretests.longmessage;

import android.content.Context;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.conversation.ConversationMessage;
import org.mycrimes.insecuretests.database.model.MessageRecord;

/**
 * A wrapper around a {@link ConversationMessage} and its extra text attachment expanded into a string
 * held in memory.
 */
class LongMessage {

  private final ConversationMessage conversationMessage;

  LongMessage(@NonNull ConversationMessage conversationMessage) {
    this.conversationMessage = conversationMessage;
  }

  @NonNull MessageRecord getMessageRecord() {
    return conversationMessage.getMessageRecord();
  }

  @NonNull CharSequence getFullBody(@NonNull Context context) {
    return conversationMessage.getDisplayBody(context);
  }
}
