package org.mycrimes.insecuretests.groups.ui;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.recipients.Recipient;

public interface RecipientClickListener {
  void onClick(@NonNull Recipient recipient);
}
