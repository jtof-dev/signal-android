package org.mycrimes.insecuretests.util

/** Kilobytes in bytes */
val Int.kb
  get() = this * 1024

/** Megabytes in bytes. */
val Int.mb
  get() = this * 1024 * 1024
