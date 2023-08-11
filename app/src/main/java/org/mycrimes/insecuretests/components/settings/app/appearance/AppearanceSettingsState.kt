package org.mycrimes.insecuretests.components.settings.app.appearance

import org.mycrimes.insecuretests.keyvalue.SettingsValues

data class AppearanceSettingsState(
  val theme: SettingsValues.Theme,
  val messageFontSize: Int,
  val language: String,
  val isCompactNavigationBar: Boolean
)
