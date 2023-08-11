package org.mycrimes.insecuretests.components.settings.app.account.export

data class ExportAccountDataState(
  val downloadInProgress: Boolean,
  val exportAsJson: Boolean,
  val showDownloadFailedDialog: Boolean = false,
  val showExportDialog: Boolean = false
)
