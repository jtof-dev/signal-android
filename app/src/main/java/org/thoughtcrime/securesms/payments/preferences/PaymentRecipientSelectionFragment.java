package org.mycrimes.insecuretests.payments.preferences;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import org.signal.core.util.concurrent.SimpleTask;
import org.mycrimes.insecuretests.ContactSelectionListFragment;
import org.mycrimes.insecuretests.LoggingFragment;
import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.components.ContactFilterView;
import org.mycrimes.insecuretests.contacts.ContactSelectionDisplayMode;
import org.mycrimes.insecuretests.conversation.ConversationIntents;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.payments.CanNotSendPaymentDialog;
import org.mycrimes.insecuretests.payments.preferences.model.PayeeParcelable;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.recipients.RecipientId;
import org.mycrimes.insecuretests.util.ViewUtil;
import org.mycrimes.insecuretests.util.navigation.SafeNavigation;
import org.whispersystems.signalservice.api.util.ExpiringProfileCredentialUtil;

import java.util.Optional;
import java.util.function.Consumer;


public class PaymentRecipientSelectionFragment extends LoggingFragment implements ContactSelectionListFragment.OnContactSelectedListener, ContactSelectionListFragment.ScrollCallback {

  private Toolbar                      toolbar;
  private ContactFilterView            contactFilterView;
  private ContactSelectionListFragment contactsFragment;

  public PaymentRecipientSelectionFragment() {
    super(R.layout.payment_recipient_selection_fragment);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    toolbar = view.findViewById(R.id.payment_recipient_selection_fragment_toolbar);
    toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());

    contactFilterView = view.findViewById(R.id.contact_filter_edit_text);

    Bundle arguments = new Bundle();
    arguments.putBoolean(ContactSelectionListFragment.REFRESHABLE, false);
    arguments.putInt(ContactSelectionListFragment.DISPLAY_MODE, ContactSelectionDisplayMode.FLAG_PUSH | ContactSelectionDisplayMode.FLAG_HIDE_NEW);
    arguments.putBoolean(ContactSelectionListFragment.CAN_SELECT_SELF, false);

    Fragment child = getChildFragmentManager().findFragmentById(R.id.contact_selection_list_fragment_holder);
    if (child == null) {
      FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
      contactsFragment = new ContactSelectionListFragment();
      contactsFragment.setArguments(arguments);
      transaction.add(R.id.contact_selection_list_fragment_holder, contactsFragment);
      transaction.commit();
    } else {
      contactsFragment = (ContactSelectionListFragment) child;
    }

    initializeSearch();
  }

  private void initializeSearch() {
    contactFilterView.setOnFilterChangedListener(filter -> contactsFragment.setQueryFilter(filter));
  }

  @Override
  public void onBeforeContactSelected(boolean isFromUnknownSearchKey, @NonNull Optional<RecipientId> recipientId, @Nullable String number, @NonNull Consumer<Boolean> callback) {
    if (recipientId.isPresent()) {
      SimpleTask.run(getViewLifecycleOwner().getLifecycle(),
                     () -> Recipient.resolved(recipientId.get()),
                     this::createPaymentOrShowWarningDialog);
    }

    callback.accept(false);
  }

  @Override
  public void onContactDeselected(@NonNull Optional<RecipientId> recipientId, @Nullable String number) {}

  @Override
  public void onSelectionChanged() {
  }

  @Override
  public void onBeginScroll() {
    hideKeyboard();
  }

  private void hideKeyboard() {
    ViewUtil.hideKeyboard(requireContext(), toolbar);
    toolbar.clearFocus();
  }

  private void createPaymentOrShowWarningDialog(@NonNull Recipient recipient) {
    if (ExpiringProfileCredentialUtil.isValid(recipient.getExpiringProfileKeyCredential())) {
      createPayment(recipient.getId());
    } else {
      showWarningDialog(recipient.getId());
    }
  }

  private void createPayment(@NonNull RecipientId recipientId) {
    hideKeyboard();
    SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), PaymentRecipientSelectionFragmentDirections.actionPaymentRecipientSelectionToCreatePayment(new PayeeParcelable(recipientId)));
  }

  private void showWarningDialog(@NonNull RecipientId recipientId) {
    CanNotSendPaymentDialog.show(requireContext(),
                                 () -> openConversation(recipientId));
  }

  private void openConversation(@NonNull RecipientId recipientId) {
    SimpleTask.run(getViewLifecycleOwner().getLifecycle(),
                   () -> SignalDatabase.threads().getThreadIdIfExistsFor(recipientId),
                   threadId -> startActivity(ConversationIntents.createBuilder(requireContext(), recipientId, threadId).build()));
  }
}
