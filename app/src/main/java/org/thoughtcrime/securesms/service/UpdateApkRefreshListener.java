package org.mycrimes.insecuretests.service;


import android.content.Context;
import android.content.Intent;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.BuildConfig;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.UpdateApkJob;
import org.mycrimes.insecuretests.util.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class UpdateApkRefreshListener extends PersistentAlarmManagerListener {

  private static final String TAG = Log.tag(UpdateApkRefreshListener.class);

  private static final long INTERVAL = TimeUnit.HOURS.toMillis(6);

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getUpdateApkRefreshTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    Log.i(TAG, "onAlarm...");

    if (scheduledTime != 0 && BuildConfig.PLAY_STORE_DISABLED) {
      Log.i(TAG, "Queueing APK update job...");
      ApplicationDependencies.getJobManager().add(new UpdateApkJob());
    }

    long newTime = System.currentTimeMillis() + INTERVAL;
    TextSecurePreferences.setUpdateApkRefreshTime(context, newTime);

    return newTime;
  }

  public static void schedule(Context context) {
    new UpdateApkRefreshListener().onReceive(context, getScheduleIntent());
  }

}
