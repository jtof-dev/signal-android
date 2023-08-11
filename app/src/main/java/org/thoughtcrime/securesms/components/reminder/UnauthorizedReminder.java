package org.mycrimes.insecuretests.components.reminder;

import android.content.Context;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.registration.RegistrationNavigationActivity;
import org.mycrimes.insecuretests.util.TextSecurePreferences;

public class UnauthorizedReminder extends Reminder {

  public UnauthorizedReminder(final Context context) {
    super(R.string.UnauthorizedReminder_this_is_likely_because_you_registered_your_phone_number_with_Signal_on_a_different_device);

    setOkListener(v -> {
      context.startActivity(RegistrationNavigationActivity.newIntentForReRegistration(context));
    });

    addAction(new Action(R.string.UnauthorizedReminder_reregister_action, R.id.reminder_action_re_register));
  }

  @Override
  public boolean isDismissable() {
    return false;
  }

  @Override
  public @NonNull Importance getImportance() {
    return Importance.ERROR;
  }

  public static boolean isEligible(Context context) {
    return TextSecurePreferences.isUnauthorizedReceived(context);
  }
}
