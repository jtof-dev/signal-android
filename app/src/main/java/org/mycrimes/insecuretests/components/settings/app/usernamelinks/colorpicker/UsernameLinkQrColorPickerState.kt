package org.mycrimes.insecuretests.components.settings.app.usernamelinks.colorpicker

import kotlinx.collections.immutable.ImmutableList
import org.mycrimes.insecuretests.components.settings.app.usernamelinks.QrCodeData
import org.mycrimes.insecuretests.components.settings.app.usernamelinks.UsernameQrCodeColorScheme

data class UsernameLinkQrColorPickerState(
  val username: String,
  val qrCodeData: QrCodeData?,
  val colorSchemes: ImmutableList<UsernameQrCodeColorScheme>,
  val selectedColorScheme: UsernameQrCodeColorScheme
)
