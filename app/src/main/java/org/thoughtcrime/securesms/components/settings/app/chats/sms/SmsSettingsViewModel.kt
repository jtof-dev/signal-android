package org.mycrimes.insecuretests.components.settings.app.chats.sms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.plusAssign
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.util.Util
import org.mycrimes.insecuretests.util.livedata.Store

class SmsSettingsViewModel : ViewModel() {

  private val repository = SmsSettingsRepository()

  private val disposables = CompositeDisposable()
  private val store = Store(
    SmsSettingsState(
      useAsDefaultSmsApp = Util.isDefaultSmsProvider(ApplicationDependencies.getApplication()),
      smsDeliveryReportsEnabled = SignalStore.settings().isSmsDeliveryReportsEnabled,
      wifiCallingCompatibilityEnabled = SignalStore.settings().isWifiCallingCompatibilityModeEnabled
    )
  )

  val state: LiveData<SmsSettingsState> = store.stateLiveData

  init {
    disposables += repository.getSmsExportState().subscribe { state ->
      store.update { it.copy(smsExportState = state) }
    }
  }

  override fun onCleared() {
    disposables.clear()
  }

  fun setSmsDeliveryReportsEnabled(enabled: Boolean) {
    store.update { it.copy(smsDeliveryReportsEnabled = enabled) }
    SignalStore.settings().isSmsDeliveryReportsEnabled = enabled
  }

  fun setWifiCallingCompatibilityEnabled(enabled: Boolean) {
    store.update { it.copy(wifiCallingCompatibilityEnabled = enabled) }
    SignalStore.settings().isWifiCallingCompatibilityModeEnabled = enabled
  }

  fun checkSmsEnabled() {
    store.update { it.copy(useAsDefaultSmsApp = Util.isDefaultSmsProvider(ApplicationDependencies.getApplication())) }
  }
}
