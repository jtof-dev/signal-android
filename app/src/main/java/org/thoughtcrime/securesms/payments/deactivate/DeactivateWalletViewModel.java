package org.mycrimes.insecuretests.payments.deactivate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.payments.Balance;
import org.mycrimes.insecuretests.payments.preferences.PaymentsHomeRepository;
import org.mycrimes.insecuretests.util.SingleLiveEvent;
import org.whispersystems.signalservice.api.payments.Money;

public class DeactivateWalletViewModel extends ViewModel {

  private final LiveData<Money>         balance;
  private final PaymentsHomeRepository  paymentsHomeRepository   = new PaymentsHomeRepository();
  private final SingleLiveEvent<Result> deactivatePaymentResults = new SingleLiveEvent<>();

  public DeactivateWalletViewModel() {
    balance = Transformations.map(SignalStore.paymentsValues().liveMobileCoinBalance(), Balance::getFullAmount);
  }

  void deactivateWallet() {
    paymentsHomeRepository.deactivatePayments(isDisabled -> deactivatePaymentResults.postValue(isDisabled ? Result.SUCCESS : Result.FAILED));
  }

  LiveData<Result> getDeactivationResults() {
    return deactivatePaymentResults;
  }

  LiveData<Money> getBalance() {
    return balance;
  }

  enum Result {
    SUCCESS,
    FAILED
  }
}
