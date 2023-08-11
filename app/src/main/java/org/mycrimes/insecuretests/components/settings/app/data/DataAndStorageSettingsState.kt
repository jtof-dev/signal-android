package org.mycrimes.insecuretests.components.settings.app.data

import org.mycrimes.insecuretests.mms.SentMediaQuality
import org.mycrimes.insecuretests.webrtc.CallDataMode

data class DataAndStorageSettingsState(
  val totalStorageUse: Long,
  val mobileAutoDownloadValues: Set<String>,
  val wifiAutoDownloadValues: Set<String>,
  val roamingAutoDownloadValues: Set<String>,
  val callDataMode: CallDataMode,
  val isProxyEnabled: Boolean,
  val sentMediaQuality: SentMediaQuality
)
