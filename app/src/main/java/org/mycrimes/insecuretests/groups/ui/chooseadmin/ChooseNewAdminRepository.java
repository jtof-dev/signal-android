package org.mycrimes.insecuretests.groups.ui.chooseadmin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.mycrimes.insecuretests.groups.GroupChangeException;
import org.mycrimes.insecuretests.groups.GroupId;
import org.mycrimes.insecuretests.groups.GroupManager;
import org.mycrimes.insecuretests.groups.ui.GroupChangeFailureReason;
import org.mycrimes.insecuretests.groups.ui.GroupChangeResult;
import org.mycrimes.insecuretests.recipients.RecipientId;

import java.io.IOException;
import java.util.List;

public final class ChooseNewAdminRepository {
  private final Application context;

  ChooseNewAdminRepository(@NonNull Application context) {
    this.context = context;
  }

  @WorkerThread
  @NonNull GroupChangeResult updateAdminsAndLeave(@NonNull GroupId.V2 groupId, @NonNull List<RecipientId> newAdminIds) {
    try {
      GroupManager.addMemberAdminsAndLeaveGroup(context, groupId, newAdminIds);
      return GroupChangeResult.SUCCESS;
    } catch (GroupChangeException | IOException e) {
      return GroupChangeResult.failure(GroupChangeFailureReason.fromException(e));
    }
  }
}
