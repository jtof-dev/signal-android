package org.mycrimes.insecuretests.registration;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.DirectoryRefreshJob;
import org.mycrimes.insecuretests.jobs.StorageSyncJob;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.recipients.Recipient;

public final class RegistrationUtil {

  private static final String TAG = Log.tag(RegistrationUtil.class);

  private RegistrationUtil() {}

  /**
   * There's several events where a registration may or may not be considered complete based on what
   * path a user has taken. This will only truly mark registration as complete if all of the
   * requirements are met.
   */
  public static void maybeMarkRegistrationComplete() {
    if (!SignalStore.registrationValues().isRegistrationComplete() &&
        SignalStore.account().isRegistered()                       &&
        !Recipient.self().getProfileName().isEmpty()               &&
        (SignalStore.kbsValues().hasPin() || SignalStore.kbsValues().hasOptedOut()))
    {
      Log.i(TAG, "Marking registration completed.", new Throwable());
      SignalStore.registrationValues().setRegistrationComplete();
      ApplicationDependencies.getJobManager().startChain(new StorageSyncJob())
                                             .then(new DirectoryRefreshJob(false))
                                             .enqueue();
    } else if (!SignalStore.registrationValues().isRegistrationComplete()) {
      Log.i(TAG, "Registration is not yet complete.", new Throwable());
    }
  }
}
