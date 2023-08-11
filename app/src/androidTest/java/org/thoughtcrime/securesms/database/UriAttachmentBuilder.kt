package org.mycrimes.insecuretests.database

import android.net.Uri
import org.mycrimes.insecuretests.attachments.UriAttachment
import org.mycrimes.insecuretests.audio.AudioHash
import org.mycrimes.insecuretests.blurhash.BlurHash
import org.mycrimes.insecuretests.stickers.StickerLocator

object UriAttachmentBuilder {
  fun build(
    id: Long,
    uri: Uri = Uri.parse("content://$id"),
    contentType: String,
    transferState: Int = AttachmentTable.TRANSFER_PROGRESS_PENDING,
    size: Long = 0L,
    fileName: String = "file$id",
    voiceNote: Boolean = false,
    borderless: Boolean = false,
    videoGif: Boolean = false,
    quote: Boolean = false,
    caption: String? = null,
    stickerLocator: StickerLocator? = null,
    blurHash: BlurHash? = null,
    audioHash: AudioHash? = null,
    transformProperties: AttachmentTable.TransformProperties? = null
  ): UriAttachment {
    return UriAttachment(
      uri,
      contentType,
      transferState,
      size,
      fileName,
      voiceNote,
      borderless,
      videoGif,
      quote,
      caption,
      stickerLocator,
      blurHash,
      audioHash,
      transformProperties
    )
  }
}
