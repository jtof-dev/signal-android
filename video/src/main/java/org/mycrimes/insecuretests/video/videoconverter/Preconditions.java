package org.mycrimes.insecuretests.video.videoconverter;

public final class Preconditions {

  private Preconditions() {}

  public static void checkState(final String errorMessage, final boolean expression) {
    if (!expression) {
      throw new IllegalStateException(errorMessage);
    }
  }
}
