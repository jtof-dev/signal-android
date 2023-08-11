package org.mycrimes.insecuretests.components.settings.app.data

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.mms.SentMediaQuality
import org.mycrimes.insecuretests.util.TextSecurePreferences
import org.mycrimes.insecuretests.util.livedata.Store
import org.mycrimes.insecuretests.webrtc.CallDataMode

class DataAndStorageSettingsViewModel(
  private val sharedPreferences: SharedPreferences,
  private val repository: DataAndStorageSettingsRepository
) : ViewModel() {

  private val store = Store(getState())

  val state: LiveData<DataAndStorageSettingsState> = store.stateLiveData

  fun refresh() {
    repository.getTotalStorageUse { totalStorageUse ->
      store.update { getState().copy(totalStorageUse = totalStorageUse) }
    }
  }

  fun setMobileAutoDownloadValues(resultSet: Set<String>) {
    sharedPreferences.edit().putStringSet(TextSecurePreferences.MEDIA_DOWNLOAD_MOBILE_PREF, resultSet).apply()
    getStateAndCopyStorageUsage()
  }

  fun setWifiAutoDownloadValues(resultSet: Set<String>) {
    sharedPreferences.edit().putStringSet(TextSecurePreferences.MEDIA_DOWNLOAD_WIFI_PREF, resultSet).apply()
    getStateAndCopyStorageUsage()
  }

  fun setRoamingAutoDownloadValues(resultSet: Set<String>) {
    sharedPreferences.edit().putStringSet(TextSecurePreferences.MEDIA_DOWNLOAD_ROAMING_PREF, resultSet).apply()
    getStateAndCopyStorageUsage()
  }

  fun setCallDataMode(callDataMode: CallDataMode) {
    SignalStore.settings().callDataMode = callDataMode
    ApplicationDependencies.getSignalCallManager().dataModeUpdate()
    getStateAndCopyStorageUsage()
  }

  fun setSentMediaQuality(sentMediaQuality: SentMediaQuality) {
    SignalStore.settings().sentMediaQuality = sentMediaQuality
    getStateAndCopyStorageUsage()
  }

  private fun getStateAndCopyStorageUsage() {
    store.update { getState().copy(totalStorageUse = it.totalStorageUse) }
  }

  private fun getState() = DataAndStorageSettingsState(
    totalStorageUse = 0,
    mobileAutoDownloadValues = TextSecurePreferences.getMobileMediaDownloadAllowed(
      ApplicationDependencies.getApplication()
    ),
    wifiAutoDownloadValues = TextSecurePreferences.getWifiMediaDownloadAllowed(
      ApplicationDependencies.getApplication()
    ),
    roamingAutoDownloadValues = TextSecurePreferences.getRoamingMediaDownloadAllowed(
      ApplicationDependencies.getApplication()
    ),
    callDataMode = SignalStore.settings().callDataMode,
    isProxyEnabled = SignalStore.proxy().isProxyEnabled,
    sentMediaQuality = SignalStore.settings().sentMediaQuality
  )

  class Factory(
    private val sharedPreferences: SharedPreferences,
    private val repository: DataAndStorageSettingsRepository
  ) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      return requireNotNull(modelClass.cast(DataAndStorageSettingsViewModel(sharedPreferences, repository)))
    }
  }
}
