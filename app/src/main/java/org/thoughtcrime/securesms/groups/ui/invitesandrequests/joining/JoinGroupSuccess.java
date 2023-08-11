package org.mycrimes.insecuretests.groups.ui.invitesandrequests.joining;

import org.mycrimes.insecuretests.recipients.Recipient;

final class JoinGroupSuccess {
  private final Recipient groupRecipient;
  private final long      groupThreadId;

  JoinGroupSuccess(Recipient groupRecipient, long groupThreadId) {
    this.groupRecipient = groupRecipient;
    this.groupThreadId  = groupThreadId;
  }

  Recipient getGroupRecipient() {
    return groupRecipient;
  }

  long getGroupThreadId() {
    return groupThreadId;
  }
}
