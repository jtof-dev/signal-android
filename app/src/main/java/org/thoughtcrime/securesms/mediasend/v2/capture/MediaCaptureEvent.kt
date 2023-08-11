package org.mycrimes.insecuretests.mediasend.v2.capture

import org.mycrimes.insecuretests.mediasend.Media

sealed class MediaCaptureEvent {
  data class MediaCaptureRendered(val media: Media) : MediaCaptureEvent()
  object MediaCaptureRenderFailed : MediaCaptureEvent()
}
