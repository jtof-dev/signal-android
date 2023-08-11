package org.mycrimes.insecuretests.registration.fragments;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.signal.core.util.concurrent.SimpleTask;
import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.NewRegistrationUsernameSyncJob;
import org.mycrimes.insecuretests.jobs.StorageAccountRestoreJob;
import org.mycrimes.insecuretests.jobs.StorageSyncJob;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.registration.viewmodel.BaseRegistrationViewModel;
import org.mycrimes.insecuretests.registration.viewmodel.RegistrationViewModel;
import org.mycrimes.insecuretests.util.CommunicationActions;
import org.mycrimes.insecuretests.util.FeatureFlags;
import org.signal.core.util.Stopwatch;
import org.mycrimes.insecuretests.util.SupportEmailUtil;
import org.mycrimes.insecuretests.util.navigation.SafeNavigation;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class RegistrationLockFragment extends BaseRegistrationLockFragment {

  private static final String TAG = Log.tag(RegistrationLockFragment.class);

  public RegistrationLockFragment() {
    super(R.layout.fragment_registration_lock);
  }

  @Override
  protected BaseRegistrationViewModel getViewModel() {
    return new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
  }

  @Override
  protected void navigateToAccountLocked() {
    SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), RegistrationLockFragmentDirections.actionAccountLocked());
  }

  @Override
  protected void handleSuccessfulPinEntry(@NonNull String pin) {
    SignalStore.pinValues().setKeyboardType(getPinEntryKeyboardType());

    SimpleTask.run(() -> {
      SignalStore.onboarding().clearAll();

      Stopwatch stopwatch = new Stopwatch("RegistrationLockRestore");

      ApplicationDependencies.getJobManager().runSynchronously(new StorageAccountRestoreJob(), StorageAccountRestoreJob.LIFESPAN);
      stopwatch.split("AccountRestore");

      ApplicationDependencies
          .getJobManager()
          .startChain(new StorageSyncJob())
          .then(new NewRegistrationUsernameSyncJob())
          .enqueueAndBlockUntilCompletion(TimeUnit.SECONDS.toMillis(10));
      stopwatch.split("ContactRestore");

      try {
        FeatureFlags.refreshSync();
      } catch (IOException e) {
        Log.w(TAG, "Failed to refresh flags.", e);
      }
      stopwatch.split("FeatureFlags");

      stopwatch.stop(TAG);

      return null;
    }, none -> {
      pinButton.cancelSpinning();
      SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), RegistrationLockFragmentDirections.actionSuccessfulRegistration());
    });
  }

  @Override
  protected void sendEmailToSupport() {
    int subject = R.string.RegistrationLockFragment__signal_registration_need_help_with_pin_for_android_v2_pin;

    String body = SupportEmailUtil.generateSupportEmailBody(requireContext(),
                                                            subject,
                                                            null,
                                                            null);
    CommunicationActions.openEmail(requireContext(),
                                   SupportEmailUtil.getSupportEmailAddress(requireContext()),
                                   getString(subject),
                                   body);
  }
}
