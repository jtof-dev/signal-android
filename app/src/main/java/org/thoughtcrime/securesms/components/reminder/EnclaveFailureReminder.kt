package org.mycrimes.insecuretests.components.reminder

import android.content.Context
import android.view.View
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.util.PlayStoreUtil

/**
 * Banner to update app to the latest version because of enclave failure
 */
class EnclaveFailureReminder(context: Context) : Reminder(R.string.EnclaveFailureReminder_update_signal) {

  init {
    addAction(Action(R.string.ExpiredBuildReminder_update_now, R.id.reminder_action_update_now))
    okListener = View.OnClickListener { PlayStoreUtil.openPlayStoreOrOurApkDownloadPage(context) }
  }

  override fun isDismissable(): Boolean = false

  override fun getImportance(): Importance {
    return Importance.TERMINAL
  }
}
