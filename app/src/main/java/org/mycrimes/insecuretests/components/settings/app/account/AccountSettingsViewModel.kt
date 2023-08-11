package org.mycrimes.insecuretests.components.settings.app.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.util.TextSecurePreferences
import org.mycrimes.insecuretests.util.livedata.Store

class AccountSettingsViewModel : ViewModel() {
  private val store: Store<AccountSettingsState> = Store(getCurrentState())

  val state: LiveData<AccountSettingsState> = store.stateLiveData

  fun refreshState() {
    store.update { getCurrentState() }
  }

  private fun getCurrentState(): AccountSettingsState {
    return AccountSettingsState(
      hasPin = SignalStore.kbsValues().hasPin() && !SignalStore.kbsValues().hasOptedOut(),
      pinRemindersEnabled = SignalStore.pinValues().arePinRemindersEnabled(),
      registrationLockEnabled = SignalStore.kbsValues().isV2RegistrationLockEnabled,
      userUnregistered = TextSecurePreferences.isUnauthorizedReceived(ApplicationDependencies.getApplication()),
      clientDeprecated = SignalStore.misc().isClientDeprecated
    )
  }
}
