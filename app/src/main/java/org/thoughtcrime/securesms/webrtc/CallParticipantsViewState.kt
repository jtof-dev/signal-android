package org.mycrimes.insecuretests.webrtc

import org.mycrimes.insecuretests.components.webrtc.CallParticipantsState
import org.mycrimes.insecuretests.service.webrtc.state.WebRtcEphemeralState

class CallParticipantsViewState(
  callParticipantsState: CallParticipantsState,
  ephemeralState: WebRtcEphemeralState,
  val isPortrait: Boolean,
  val isLandscapeEnabled: Boolean
) {

  val callParticipantsState = CallParticipantsState.update(callParticipantsState, ephemeralState)
}
