package org.mycrimes.insecuretests.components.settings.app.privacy

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.jobs.MultiDeviceConfigurationUpdateJob
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.storage.StorageSyncHelper
import org.mycrimes.insecuretests.util.TextSecurePreferences

class PrivacySettingsRepository {

  private val context: Context = ApplicationDependencies.getApplication()

  fun getBlockedCount(consumer: (Int) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val recipientDatabase = SignalDatabase.recipients

      consumer(recipientDatabase.getBlocked().count)
    }
  }

  fun syncReadReceiptState() {
    SignalExecutors.BOUNDED.execute {
      SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
      StorageSyncHelper.scheduleSyncForDataChange()
      ApplicationDependencies.getJobManager().add(
        MultiDeviceConfigurationUpdateJob(
          TextSecurePreferences.isReadReceiptsEnabled(context),
          TextSecurePreferences.isTypingIndicatorsEnabled(context),
          TextSecurePreferences.isShowUnidentifiedDeliveryIndicatorsEnabled(context),
          SignalStore.settings().isLinkPreviewsEnabled
        )
      )
    }
  }

  fun syncTypingIndicatorsState() {
    val enabled = TextSecurePreferences.isTypingIndicatorsEnabled(context)

    SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
    StorageSyncHelper.scheduleSyncForDataChange()
    ApplicationDependencies.getJobManager().add(
      MultiDeviceConfigurationUpdateJob(
        TextSecurePreferences.isReadReceiptsEnabled(context),
        enabled,
        TextSecurePreferences.isShowUnidentifiedDeliveryIndicatorsEnabled(context),
        SignalStore.settings().isLinkPreviewsEnabled
      )
    )

    if (!enabled) {
      ApplicationDependencies.getTypingStatusRepository().clear()
    }
  }
}
