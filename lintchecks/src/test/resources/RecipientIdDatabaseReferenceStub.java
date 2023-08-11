package org.mycrimes.insecuretests.database;

interface RecipientIdDatabaseReference {
  void remapRecipient(RecipientId fromId, RecipientId toId);
}
