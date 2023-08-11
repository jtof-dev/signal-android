package org.mycrimes.insecuretests.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.mycrimes.insecuretests.contacts.ContactsSyncAdapter;

public class ContactsSyncAdapterService extends Service {

  private static ContactsSyncAdapter syncAdapter;

  @Override
  public synchronized void onCreate() {
    if (syncAdapter == null) {
      syncAdapter = new ContactsSyncAdapter(this, true);
    }
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return syncAdapter.getSyncAdapterBinder();
  }
}
