package org.mycrimes.insecuretests.pin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.mycrimes.insecuretests.MainActivity;
import org.mycrimes.insecuretests.PassphraseRequiredActivity;
import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.lock.v2.CreateKbsPinActivity;
import org.mycrimes.insecuretests.util.DynamicNoActionBarTheme;
import org.mycrimes.insecuretests.util.DynamicTheme;

public final class PinRestoreActivity extends AppCompatActivity {

  private final DynamicTheme dynamicTheme = new DynamicNoActionBarTheme();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    dynamicTheme.onCreate(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pin_restore_activity);
  }

  @Override
  protected void onResume() {
    super.onResume();
    dynamicTheme.onResume(this);
  }

  void navigateToPinCreation() {
    final Intent main      = MainActivity.clearTop(this);
    final Intent createPin = CreateKbsPinActivity.getIntentForPinCreate(this);
    final Intent chained   = PassphraseRequiredActivity.chainIntent(createPin, main);

    startActivity(chained);
    finish();
  }
}
