package org.mycrimes.insecuretests.components.settings.conversation.preferences

import android.content.Context
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.util.DateUtils
import java.util.Locale

object Utils {

  fun Long.formatMutedUntil(context: Context): String {
    return if (this == Long.MAX_VALUE) {
      context.getString(R.string.ConversationSettingsFragment__conversation_muted_forever)
    } else {
      context.getString(
        R.string.ConversationSettingsFragment__conversation_muted_until_s,
        DateUtils.getTimeString(context, Locale.getDefault(), this)
      )
    }
  }
}
