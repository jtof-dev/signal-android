package org.mycrimes.insecuretests.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.PushNotificationReceiveJob;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    ApplicationDependencies.getJobManager().add(new PushNotificationReceiveJob());
  }
}
