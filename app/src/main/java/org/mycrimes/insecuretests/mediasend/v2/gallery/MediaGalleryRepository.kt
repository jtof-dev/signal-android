package org.mycrimes.insecuretests.mediasend.v2.gallery

import android.content.Context
import org.mycrimes.insecuretests.mediasend.Media
import org.mycrimes.insecuretests.mediasend.MediaFolder
import org.mycrimes.insecuretests.mediasend.MediaRepository

class MediaGalleryRepository(context: Context, private val mediaRepository: MediaRepository) {
  private val context: Context = context.applicationContext

  fun getFolders(onFoldersRetrieved: (List<MediaFolder>) -> Unit) {
    mediaRepository.getFolders(context) { onFoldersRetrieved(it) }
  }

  fun getMedia(bucketId: String, onMediaRetrieved: (List<Media>) -> Unit) {
    mediaRepository.getMediaInBucket(context, bucketId) { onMediaRetrieved(it) }
  }
}
