package org.mycrimes.insecuretests.components.webrtc;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.util.Consumer;

import org.signal.core.util.concurrent.SignalExecutors;
import org.mycrimes.insecuretests.database.GroupTable;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.database.identity.IdentityRecordList;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.recipients.Recipient;

import java.util.Collections;
import java.util.List;

class WebRtcCallRepository {

  private final Context      context;

  WebRtcCallRepository(@NonNull Context context) {
    this.context      = context;
  }

  @WorkerThread
  void getIdentityRecords(@NonNull Recipient recipient, @NonNull Consumer<IdentityRecordList> consumer) {
    SignalExecutors.BOUNDED.execute(() -> {
      List<Recipient> recipients;

      if (recipient.isGroup()) {
        recipients = SignalDatabase.groups().getGroupMembers(recipient.requireGroupId(), GroupTable.MemberSet.FULL_MEMBERS_EXCLUDING_SELF);
      } else {
        recipients = Collections.singletonList(recipient);
      }

      consumer.accept(ApplicationDependencies.getProtocolStore().aci().identities().getIdentityRecords(recipients));
    });
  }
}
