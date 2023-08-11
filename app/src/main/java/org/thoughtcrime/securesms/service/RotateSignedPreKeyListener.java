package org.mycrimes.insecuretests.service;


import android.content.Context;
import android.content.Intent;

import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.PreKeysSyncJob;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.util.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class RotateSignedPreKeyListener extends PersistentAlarmManagerListener {

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getSignedPreKeyRotationTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    if (scheduledTime != 0 && SignalStore.account().isRegistered()) {
      PreKeysSyncJob.enqueue();
    }

    long nextTime = System.currentTimeMillis() + PreKeysSyncJob.REFRESH_INTERVAL;
    TextSecurePreferences.setSignedPreKeyRotationTime(context, nextTime);

    return nextTime;
  }

  public static void schedule(Context context) {
    new RotateSignedPreKeyListener().onReceive(context, getScheduleIntent());
  }
}
