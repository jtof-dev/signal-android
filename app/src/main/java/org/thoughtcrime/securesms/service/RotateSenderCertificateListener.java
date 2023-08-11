package org.mycrimes.insecuretests.service;


import android.content.Context;
import android.content.Intent;

import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.RotateCertificateJob;
import org.mycrimes.insecuretests.util.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class RotateSenderCertificateListener extends PersistentAlarmManagerListener {

  private static final long INTERVAL = TimeUnit.DAYS.toMillis(1);

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getUnidentifiedAccessCertificateRotationTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    ApplicationDependencies.getJobManager().add(new RotateCertificateJob());

    long nextTime = System.currentTimeMillis() + INTERVAL;
    TextSecurePreferences.setUnidentifiedAccessCertificateRotationTime(context, nextTime);

    return nextTime;
  }

  public static void schedule(Context context) {
    new RotateSenderCertificateListener().onReceive(context, getScheduleIntent());
  }

}
