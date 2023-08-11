package org.mycrimes.insecuretests;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import org.mycrimes.insecuretests.conversationlist.model.ConversationSet;
import org.mycrimes.insecuretests.database.model.ThreadRecord;
import org.mycrimes.insecuretests.mms.GlideRequests;

import java.util.Locale;
import java.util.Set;

public interface BindableConversationListItem extends Unbindable {

  void bind(@NonNull LifecycleOwner lifecycleOwner,
            @NonNull ThreadRecord thread,
            @NonNull GlideRequests glideRequests, @NonNull Locale locale,
            @NonNull Set<Long> typingThreads,
            @NonNull ConversationSet selectedConversations);

  void setSelectedConversations(@NonNull ConversationSet conversations);
  void updateTypingIndicator(@NonNull Set<Long> typingThreads);
}
