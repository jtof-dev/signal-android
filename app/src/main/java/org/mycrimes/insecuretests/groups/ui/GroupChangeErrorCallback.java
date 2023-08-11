package org.mycrimes.insecuretests.groups.ui;

import androidx.annotation.NonNull;

public interface GroupChangeErrorCallback {
  void onError(@NonNull GroupChangeFailureReason failureReason);
}
