package org.mycrimes.insecuretests.groups.ui;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.recipients.Recipient;

public interface RecipientLongClickListener {
  boolean onLongClick(@NonNull Recipient recipient);
}
