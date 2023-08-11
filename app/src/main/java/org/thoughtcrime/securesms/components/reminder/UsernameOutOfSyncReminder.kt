package org.mycrimes.insecuretests.components.reminder

import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.util.FeatureFlags

/**
 * Displays a reminder message when the local username gets out of sync with
 * what the server thinks our username is.
 */
class UsernameOutOfSyncReminder : Reminder(R.string.UsernameOutOfSyncReminder__something_went_wrong) {

  init {
    addAction(
      Action(
        R.string.UsernameOutOfSyncReminder__fix_now,
        R.id.reminder_action_fix_username
      )
    )
  }

  override fun isDismissable(): Boolean {
    return false
  }

  companion object {
    @JvmStatic
    fun isEligible(): Boolean {
      return FeatureFlags.usernames() && SignalStore.phoneNumberPrivacy().isUsernameOutOfSync
    }
  }
}
