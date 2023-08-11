/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.mycrimes.insecuretests.jobs

import org.signal.core.util.concurrent.safeBlockingGet
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.jobmanager.Job
import org.mycrimes.insecuretests.jobmanager.impl.NetworkConstraint
import org.mycrimes.insecuretests.service.webrtc.links.CallLinkCredentials
import org.mycrimes.insecuretests.service.webrtc.links.ReadCallLinkResult
import org.mycrimes.insecuretests.service.webrtc.links.SignalCallLinkManager
import org.whispersystems.signalservice.internal.push.SignalServiceProtos.SyncMessage.CallLinkUpdate
import java.util.concurrent.TimeUnit

/**
 * Requests the latest call link state from the call service.
 */
class RefreshCallLinkDetailsJob private constructor(
  parameters: Parameters,
  private val callLinkUpdate: CallLinkUpdate
) : BaseJob(parameters) {

  constructor(callLinkUpdate: CallLinkUpdate) : this(
    Parameters.Builder()
      .addConstraint(NetworkConstraint.KEY)
      .setQueue("__RefreshCallLinkDetailsJob__")
      .setLifespan(TimeUnit.DAYS.toMillis(1))
      .setMaxAttempts(Parameters.UNLIMITED)
      .build(),
    callLinkUpdate
  )

  companion object {
    const val KEY = "RefreshCallLinkDetailsJob"
  }

  override fun serialize(): ByteArray = callLinkUpdate.toByteArray()

  override fun getFactoryKey(): String = KEY

  override fun onFailure() = Unit

  override fun onRun() {
    val manager: SignalCallLinkManager = ApplicationDependencies.getSignalCallManager().callLinkManager
    val credentials = CallLinkCredentials(
      linkKeyBytes = callLinkUpdate.rootKey.toByteArray(),
      adminPassBytes = callLinkUpdate.adminPassKey?.toByteArray()
    )

    when (val result = manager.readCallLink(credentials).safeBlockingGet()) {
      is ReadCallLinkResult.Success -> {
        SignalDatabase.callLinks.updateCallLinkState(credentials.roomId, result.callLinkState)
      }
      else -> Unit
    }
  }

  override fun onShouldRetry(e: Exception): Boolean = false

  class Factory : Job.Factory<RefreshCallLinkDetailsJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): RefreshCallLinkDetailsJob {
      val callLinkUpdate = CallLinkUpdate.parseFrom(serializedData)
      return RefreshCallLinkDetailsJob(parameters, callLinkUpdate)
    }
  }
}
