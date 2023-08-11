package org.mycrimes.insecuretests.registration.fragments;

import androidx.lifecycle.ViewModelProvider;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.registration.viewmodel.BaseRegistrationViewModel;
import org.mycrimes.insecuretests.registration.viewmodel.RegistrationViewModel;

public class AccountLockedFragment extends BaseAccountLockedFragment {

  public AccountLockedFragment() {
    super(R.layout.account_locked_fragment);
  }

  @Override
  protected BaseRegistrationViewModel getViewModel() {
    return new ViewModelProvider(requireActivity()).get(RegistrationViewModel.class);
  }

  @Override
  protected void onNext() {
    requireActivity().finish();
  }
}
