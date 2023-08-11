package org.mycrimes.insecuretests.payments.backup.phrase;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobs.PaymentLedgerUpdateJob;
import org.mycrimes.insecuretests.jobs.ProfileUploadJob;
import org.mycrimes.insecuretests.keyvalue.PaymentsValues;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.util.Util;

import java.util.List;

class PaymentsRecoveryPhraseRepository {

  private static final String TAG = Log.tag(PaymentsRecoveryPhraseRepository.class);

  void restoreMnemonic(@NonNull List<String> words,
                       @NonNull Consumer<PaymentsValues.WalletRestoreResult> resultConsumer)
  {
    SignalExecutors.BOUNDED.execute(() -> {
      String                             mnemonic = Util.join(words, " ");
      PaymentsValues.WalletRestoreResult result   = SignalStore.paymentsValues().restoreWallet(mnemonic);

      switch (result) {
        case ENTROPY_CHANGED:
          Log.i(TAG, "restoreMnemonic: mnemonic resulted in entropy mismatch, flushing cached values");
          SignalDatabase.payments().deleteAll();
          ApplicationDependencies.getPayments().closeWallet();
          updateProfileAndFetchLedger();
          break;
        case ENTROPY_UNCHANGED:
          Log.i(TAG, "restoreMnemonic: mnemonic resulted in entropy match, no flush needed.");
          updateProfileAndFetchLedger();
          break;
        case MNEMONIC_ERROR:
          Log.w(TAG, "restoreMnemonic: failed to restore wallet from given mnemonic.");
          break;
      }

      resultConsumer.accept(result);
    });
  }

  private void updateProfileAndFetchLedger() {
    ApplicationDependencies.getJobManager()
                           .startChain(new ProfileUploadJob())
                           .then(PaymentLedgerUpdateJob.updateLedger())
                           .enqueue();
  }
}
