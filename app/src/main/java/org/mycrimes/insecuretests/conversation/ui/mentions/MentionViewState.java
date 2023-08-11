package org.mycrimes.insecuretests.conversation.ui.mentions;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.util.viewholders.RecipientMappingModel;

public final class MentionViewState extends RecipientMappingModel<MentionViewState> {

  private final Recipient recipient;

  public MentionViewState(@NonNull Recipient recipient) {
    this.recipient = recipient;
  }

  @Override
  public @NonNull Recipient getRecipient() {
    return recipient;
  }
}
