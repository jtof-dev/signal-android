package org.mycrimes.insecuretests.groups;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.recipients.RecipientId;

import java.util.Collection;
import java.util.HashSet;

public final class GroupsV2CapabilityChecker {

  private static final String TAG = Log.tag(GroupsV2CapabilityChecker.class);

  private GroupsV2CapabilityChecker() {}

  @WorkerThread
  static boolean allAndSelfHaveServiceId(@NonNull Collection<RecipientId> recipientIds) {
    HashSet<RecipientId> recipientIdsSet = new HashSet<>(recipientIds);

    recipientIdsSet.add(Recipient.self().getId());

    return allHaveServiceId(recipientIdsSet);
  }

  @WorkerThread
  static boolean allHaveServiceId(@NonNull Collection<RecipientId> recipientIds) {
    return Recipient.resolvedList(recipientIds)
                    .stream()
                    .allMatch(Recipient::hasServiceId);
  }
}
