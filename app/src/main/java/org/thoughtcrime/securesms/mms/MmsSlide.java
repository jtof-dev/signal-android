package org.mycrimes.insecuretests.mms;


import android.content.Context;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.attachments.Attachment;

public class MmsSlide extends ImageSlide {

  public MmsSlide(@NonNull Context context, @NonNull Attachment attachment) {
    super(attachment);
  }

  @NonNull
  @Override
  public String getContentDescription(Context context) {
    return "MMS";
  }

}
