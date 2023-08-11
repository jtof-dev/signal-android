package org.mycrimes.insecuretests.video;

public final class TranscoderOptions {
  final long startTimeUs;
  final long endTimeUs;

  public TranscoderOptions(long startTimeUs, long endTimeUs) {
    this.startTimeUs = startTimeUs;
    this.endTimeUs   = endTimeUs;
  }
}
