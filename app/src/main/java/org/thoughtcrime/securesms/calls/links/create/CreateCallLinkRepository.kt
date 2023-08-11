/**
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.mycrimes.insecuretests.calls.links.create

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.conversation.colors.AvatarColor
import org.mycrimes.insecuretests.database.CallLinkTable
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.service.webrtc.links.CallLinkCredentials
import org.mycrimes.insecuretests.service.webrtc.links.CreateCallLinkResult
import org.mycrimes.insecuretests.service.webrtc.links.SignalCallLinkManager

/**
 * Repository for creating new call links. This will delegate to the [SignalCallLinkManager]
 * but will also ensure the database is updated.
 */
class CreateCallLinkRepository(
  private val callLinkManager: SignalCallLinkManager = ApplicationDependencies.getSignalCallManager().callLinkManager
) {
  fun ensureCallLinkCreated(credentials: CallLinkCredentials, avatarColor: AvatarColor): Single<EnsureCallLinkCreatedResult> {
    val callLinkRecipientId = Single.fromCallable {
      SignalDatabase.recipients.getByCallLinkRoomId(credentials.roomId)
    }

    return callLinkRecipientId.flatMap { recipientId ->
      if (recipientId.isPresent) {
        Single.just(EnsureCallLinkCreatedResult.Success(Recipient.resolved(recipientId.get())))
      } else {
        callLinkManager.createCallLink(credentials).map {
          when (it) {
            is CreateCallLinkResult.Success -> {
              SignalDatabase.callLinks.insertCallLink(
                CallLinkTable.CallLink(
                  recipientId = RecipientId.UNKNOWN,
                  roomId = credentials.roomId,
                  credentials = credentials,
                  state = it.state,
                  avatarColor = avatarColor
                )
              )

              EnsureCallLinkCreatedResult.Success(
                Recipient.resolved(
                  SignalDatabase.recipients.getByCallLinkRoomId(credentials.roomId).get()
                )
              )
            }

            is CreateCallLinkResult.Failure -> EnsureCallLinkCreatedResult.Failure(it)
          }
        }
      }
    }.subscribeOn(Schedulers.io())
  }
}
