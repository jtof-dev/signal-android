package org.mycrimes.insecuretests.deeplinks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.mycrimes.insecuretests.MainActivity;
import org.mycrimes.insecuretests.PassphraseRequiredActivity;

public class DeepLinkEntryActivity extends PassphraseRequiredActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState, boolean ready) {
    Intent intent = MainActivity.clearTop(this);
    Uri    data   = getIntent().getData();
    intent.setData(data);
    startActivity(intent);
  }
}
