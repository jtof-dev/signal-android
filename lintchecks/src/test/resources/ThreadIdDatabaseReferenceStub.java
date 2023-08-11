package org.mycrimes.insecuretests.database;

interface ThreadIdDatabaseReference {
  void remapThread(long fromId, long toId);
}
