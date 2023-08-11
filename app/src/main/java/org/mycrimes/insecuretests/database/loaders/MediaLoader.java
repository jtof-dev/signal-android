package org.mycrimes.insecuretests.database.loaders;

import android.content.Context;

import org.mycrimes.insecuretests.util.AbstractCursorLoader;

public abstract class MediaLoader extends AbstractCursorLoader {

  MediaLoader(Context context) {
    super(context);
  }

  public enum MediaType {
    GALLERY,
    DOCUMENT,
    AUDIO,
    ALL
  }
}
