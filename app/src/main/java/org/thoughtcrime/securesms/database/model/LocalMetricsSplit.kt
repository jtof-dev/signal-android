package org.mycrimes.insecuretests.database.model

data class LocalMetricsSplit(
  val name: String,
  val duration: Long
) {
  override fun toString(): String {
    return "$name: $duration"
  }
}
