package org.mycrimes.insecuretests.lock.v2;

import android.content.Context;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.util.TextSecurePreferences;

public final class RegistrationLockUtil {

  private RegistrationLockUtil() {}

  public static boolean userHasRegistrationLock(@NonNull Context context) {
    return TextSecurePreferences.isV1RegistrationLockEnabled(context) || SignalStore.kbsValues().isV2RegistrationLockEnabled();
  }
}
