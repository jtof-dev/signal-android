/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.mycrimes.insecuretests.service.webrtc

import org.signal.core.util.logging.Log
import org.signal.libsignal.zkgroup.GenericServerPublicParams
import org.signal.libsignal.zkgroup.InvalidInputException
import org.signal.libsignal.zkgroup.VerificationFailedException
import org.signal.libsignal.zkgroup.calllinks.CallLinkSecretParams
import org.signal.ringrtc.CallException
import org.signal.ringrtc.CallLinkRootKey
import org.mycrimes.insecuretests.database.SignalDatabase.Companion.callLinks
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.events.WebRtcViewModel
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.ringrtc.RemotePeer
import org.mycrimes.insecuretests.service.webrtc.RingRtcDynamicConfiguration.getAudioProcessingMethod
import org.mycrimes.insecuretests.service.webrtc.state.WebRtcServiceState
import org.mycrimes.insecuretests.util.NetworkUtil
import java.io.IOException

/**
 * Process actions while the user is in the pre-join lobby for the call link.
 */
class CallLinkPreJoinActionProcessor(
  actionProcessorFactory: MultiPeerActionProcessorFactory,
  webRtcInteractor: WebRtcInteractor
) : GroupPreJoinActionProcessor(actionProcessorFactory, webRtcInteractor, TAG) {

  companion object {
    private val TAG = Log.tag(CallLinkPreJoinActionProcessor::class.java)
  }

  override fun handlePreJoinCall(currentState: WebRtcServiceState, remotePeer: RemotePeer): WebRtcServiceState {
    Log.i(TAG, "handlePreJoinCall():")

    val groupCall = try {
      val callLink = callLinks.getCallLinkByRoomId(remotePeer.recipient.requireCallLinkRoomId())
      if (callLink?.credentials == null) {
        return groupCallFailure(currentState, "No access to this call link.", Exception())
      }

      val callLinkRootKey = CallLinkRootKey(callLink.credentials.linkKeyBytes)
      val callLinkSecretParams = CallLinkSecretParams.deriveFromRootKey(callLink.credentials.linkKeyBytes)
      val genericServerPublicParams = GenericServerPublicParams(
        ApplicationDependencies.getSignalServiceNetworkAccess()
          .getConfiguration()
          .genericServerPublicParams
      )

      val callLinkAuthCredentialPresentation = ApplicationDependencies
        .getGroupsV2Authorization()
        .getCallLinkAuthorizationForToday(genericServerPublicParams, callLinkSecretParams)

      webRtcInteractor.callManager.createCallLinkCall(
        SignalStore.internalValues().groupCallingServer(),
        callLinkAuthCredentialPresentation.serialize(),
        callLinkRootKey,
        callLink.credentials.adminPassBytes,
        ByteArray(0),
        AUDIO_LEVELS_INTERVAL,
        getAudioProcessingMethod(),
        webRtcInteractor.groupCallObserver
      )
    } catch (e: InvalidInputException) {
      return groupCallFailure(currentState, "Failed to create server public parameters.", e)
    } catch (e: IOException) {
      return groupCallFailure(currentState, "Failed to get call link authorization", e)
    } catch (e: VerificationFailedException) {
      return groupCallFailure(currentState, "Failed to get call link authorization", e)
    } catch (e: CallException) {
      return groupCallFailure(currentState, "Failed to parse call link root key", e)
    } ?: return groupCallFailure(currentState, "Failed to create group call object for call link.", Exception())

    try {
      groupCall.setOutgoingAudioMuted(true)
      groupCall.setOutgoingVideoMuted(true)
      groupCall.setDataMode(NetworkUtil.getCallingDataMode(context, groupCall.localDeviceState.networkRoute.localAdapterType))
      Log.i(TAG, "Connecting to group call: " + currentState.callInfoState.callRecipient.id)
      groupCall.connect()
    } catch (e: CallException) {
      return groupCallFailure(currentState, "Unable to connect to call link", e)
    }

    SignalStore.tooltips().markGroupCallingLobbyEntered()
    return currentState.builder()
      .changeCallInfoState()
      .groupCall(groupCall)
      .groupCallState(WebRtcViewModel.GroupCallState.DISCONNECTED)
      .activePeer(RemotePeer(currentState.callInfoState.callRecipient.id, RemotePeer.GROUP_CALL_ID))
      .build()
  }

  override fun handleGroupRequestUpdateMembers(currentState: WebRtcServiceState): WebRtcServiceState {
    Log.i(tag, "handleGroupRequestUpdateMembers():")

    return currentState
  }
}