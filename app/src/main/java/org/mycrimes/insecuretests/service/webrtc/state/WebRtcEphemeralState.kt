package org.mycrimes.insecuretests.service.webrtc.state

import org.mycrimes.insecuretests.events.CallParticipant
import org.mycrimes.insecuretests.events.CallParticipantId

/**
 * The state of the call system which contains data which changes frequently.
 */
data class WebRtcEphemeralState(
  val localAudioLevel: CallParticipant.AudioLevel = CallParticipant.AudioLevel.LOWEST,
  val remoteAudioLevels: Map<CallParticipantId, CallParticipant.AudioLevel> = emptyMap()
)
