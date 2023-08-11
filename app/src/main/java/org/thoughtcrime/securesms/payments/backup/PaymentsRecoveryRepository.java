package org.mycrimes.insecuretests.payments.backup;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.payments.Mnemonic;

public final class PaymentsRecoveryRepository {
  public @NonNull Mnemonic getMnemonic() {
    return SignalStore.paymentsValues().getPaymentsMnemonic();
  }
}
