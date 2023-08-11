/**
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.mycrimes.insecuretests.calls.links.details

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.orNull
import org.mycrimes.insecuretests.database.CallLinkTable
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.service.webrtc.links.CallLinkRoomId
import org.mycrimes.insecuretests.service.webrtc.links.ReadCallLinkResult
import org.mycrimes.insecuretests.service.webrtc.links.SignalCallLinkManager

class CallLinkDetailsRepository(
  private val callLinkManager: SignalCallLinkManager = ApplicationDependencies.getSignalCallManager().callLinkManager
) {
  fun refreshCallLinkState(callLinkRoomId: CallLinkRoomId): Disposable {
    return Maybe.fromCallable<CallLinkTable.CallLink> { SignalDatabase.callLinks.getCallLinkByRoomId(callLinkRoomId) }
      .flatMapSingle { callLinkManager.readCallLink(it.credentials!!) }
      .subscribeOn(Schedulers.io())
      .subscribeBy { result ->
        when (result) {
          is ReadCallLinkResult.Success -> SignalDatabase.callLinks.updateCallLinkState(callLinkRoomId, result.callLinkState)
          is ReadCallLinkResult.Failure -> Unit
        }
      }
  }

  fun watchCallLinkRecipient(callLinkRoomId: CallLinkRoomId): Observable<Recipient> {
    return Maybe.fromCallable<RecipientId> { SignalDatabase.recipients.getByCallLinkRoomId(callLinkRoomId).orNull() }
      .flatMapObservable { Recipient.observable(it) }
      .distinctUntilChanged { a, b -> a.hasSameContent(b) }
      .subscribeOn(Schedulers.io())
  }
}
