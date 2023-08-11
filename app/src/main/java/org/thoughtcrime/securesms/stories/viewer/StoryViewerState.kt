package org.mycrimes.insecuretests.stories.viewer

import android.net.Uri
import org.mycrimes.insecuretests.blurhash.BlurHash
import org.mycrimes.insecuretests.database.model.MmsMessageRecord
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.stories.StoryTextPostModel

data class StoryViewerState(
  val pages: List<RecipientId> = emptyList(),
  val previousPage: Int = -1,
  val page: Int = -1,
  val crossfadeSource: CrossfadeSource,
  val crossfadeTarget: CrossfadeTarget? = null,
  val skipCrossfade: Boolean = false,
  val noPosts: Boolean = false
) {
  sealed class CrossfadeSource {
    object None : CrossfadeSource()
    class ImageUri(val imageUri: Uri, val imageBlur: BlurHash?) : CrossfadeSource()
    class TextModel(val storyTextPostModel: StoryTextPostModel) : CrossfadeSource()
  }

  sealed class CrossfadeTarget {
    object None : CrossfadeTarget()
    data class Record(val messageRecord: MmsMessageRecord) : CrossfadeTarget()
  }
}
