package org.mycrimes.insecuretests.service.webrtc.state

import org.mycrimes.insecuretests.components.sensors.Orientation
import org.mycrimes.insecuretests.ringrtc.CameraState
import org.mycrimes.insecuretests.webrtc.audio.SignalAudioManager
import org.webrtc.PeerConnection

/**
 * Local device specific state.
 */
data class LocalDeviceState constructor(
  var cameraState: CameraState = CameraState.UNKNOWN,
  var isMicrophoneEnabled: Boolean = true,
  var orientation: Orientation = Orientation.PORTRAIT_BOTTOM_EDGE,
  var isLandscapeEnabled: Boolean = false,
  var deviceOrientation: Orientation = Orientation.PORTRAIT_BOTTOM_EDGE,
  var activeDevice: SignalAudioManager.AudioDevice = SignalAudioManager.AudioDevice.NONE,
  var availableDevices: Set<SignalAudioManager.AudioDevice> = emptySet(),
  var bluetoothPermissionDenied: Boolean = false,
  var networkConnectionType: PeerConnection.AdapterType = PeerConnection.AdapterType.UNKNOWN
) {

  fun duplicate(): LocalDeviceState {
    return copy()
  }
}
