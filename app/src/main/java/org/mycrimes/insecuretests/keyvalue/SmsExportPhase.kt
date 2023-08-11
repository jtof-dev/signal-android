package org.mycrimes.insecuretests.keyvalue

import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.util.Util
import kotlin.time.Duration.Companion.days

enum class SmsExportPhase(val duration: Long) {
  PHASE_1(0.days.inWholeMilliseconds),
  PHASE_2(21.days.inWholeMilliseconds),
  PHASE_3(51.days.inWholeMilliseconds);

  fun allowSmsFeatures(): Boolean {
    return Util.isDefaultSmsProvider(ApplicationDependencies.getApplication()) && SignalStore.misc().smsExportPhase.isSmsSupported()
  }

  fun isSmsSupported(): Boolean {
    return this != PHASE_3
  }

  fun isFullscreen(): Boolean {
    return this.ordinal > PHASE_1.ordinal
  }

  fun isBlockingUi(): Boolean {
    return this == PHASE_3
  }

  companion object {
    @JvmStatic
    fun getCurrentPhase(duration: Long): SmsExportPhase {
      return values().findLast { duration >= it.duration }!!
    }
  }
}
