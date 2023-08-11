package org.mycrimes.insecuretests.mediasend.v2.gallery

import org.mycrimes.insecuretests.util.adapter.mapping.MappingModel

data class MediaGalleryState(
  val bucketId: String?,
  val bucketTitle: String?,
  val items: List<MappingModel<*>> = listOf()
)
