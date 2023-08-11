package org.mycrimes.insecuretests.util;

import androidx.annotation.StyleRes;

import org.mycrimes.insecuretests.R;

public class DynamicDarkActionBarTheme extends DynamicTheme {

  protected @StyleRes int getTheme() {
    return R.style.Signal_DayNight_DarkActionBar;
  }
}
