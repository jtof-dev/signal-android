package org.mycrimes.insecuretests.mediasend.v2.text.send

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.ThreadUtil
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.contacts.paged.ContactSearchKey
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.StoryType
import org.mycrimes.insecuretests.database.model.databaseprotos.StoryTextPost
import org.mycrimes.insecuretests.fonts.TextFont
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.keyvalue.StorySend
import org.mycrimes.insecuretests.linkpreview.LinkPreview
import org.mycrimes.insecuretests.mediasend.v2.UntrustedRecords
import org.mycrimes.insecuretests.mediasend.v2.text.TextStoryPostCreationState
import org.mycrimes.insecuretests.mms.OutgoingMessage
import org.mycrimes.insecuretests.providers.BlobProvider
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.stories.Stories
import org.mycrimes.insecuretests.util.Base64
import java.io.ByteArrayOutputStream

private val TAG = Log.tag(TextStoryPostSendRepository::class.java)

class TextStoryPostSendRepository {

  fun compressToBlob(bitmap: Bitmap): Single<Uri> {
    return Single.fromCallable {
      val outputStream = ByteArrayOutputStream()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
      bitmap.recycle()
      BlobProvider.getInstance().forData(outputStream.toByteArray()).createForSingleUseInMemory()
    }.subscribeOn(Schedulers.computation())
  }

  fun send(contactSearchKey: Set<ContactSearchKey>, textStoryPostCreationState: TextStoryPostCreationState, linkPreview: LinkPreview?, identityChangesSince: Long): Single<TextStoryPostSendResult> {
    return UntrustedRecords
      .checkForBadIdentityRecords(contactSearchKey.filterIsInstance(ContactSearchKey.RecipientSearchKey::class.java).toSet(), identityChangesSince)
      .toSingleDefault<TextStoryPostSendResult>(TextStoryPostSendResult.Success)
      .onErrorReturn {
        if (it is UntrustedRecords.UntrustedRecordsException) {
          TextStoryPostSendResult.UntrustedRecordsError(it.untrustedRecords)
        } else {
          Log.w(TAG, "Unexpected error occurred", it)
          TextStoryPostSendResult.Failure
        }
      }
      .flatMap { result ->
        if (result is TextStoryPostSendResult.Success) {
          performSend(contactSearchKey, textStoryPostCreationState, linkPreview)
        } else {
          Single.just(result)
        }
      }
  }

  private fun performSend(contactSearchKey: Set<ContactSearchKey>, textStoryPostCreationState: TextStoryPostCreationState, linkPreview: LinkPreview?): Single<TextStoryPostSendResult> {
    return Single.fromCallable {
      val messages: MutableList<OutgoingMessage> = mutableListOf()
      val distributionListSentTimestamp = System.currentTimeMillis()

      for (contact in contactSearchKey) {
        val recipient = Recipient.resolved(contact.requireShareContact().recipientId.get())
        val isStory = contact.requireRecipientSearchKey().isStory || recipient.isDistributionList

        if (isStory && !recipient.isMyStory) {
          SignalStore.storyValues().setLatestStorySend(StorySend.newSend(recipient))
        }

        val storyType: StoryType = when {
          recipient.isDistributionList -> SignalDatabase.distributionLists.getStoryType(recipient.requireDistributionListId())
          isStory -> StoryType.STORY_WITH_REPLIES
          else -> StoryType.NONE
        }

        val message = OutgoingMessage(
          recipient = recipient,
          body = serializeTextStoryState(textStoryPostCreationState),
          timestamp = if (recipient.isDistributionList) distributionListSentTimestamp else System.currentTimeMillis(),
          storyType = storyType.toTextStoryType(),
          previews = listOfNotNull(linkPreview),
          isSecure = true
        )

        messages.add(message)
        ThreadUtil.sleep(5)
      }

      Stories.sendTextStories(messages)
    }.flatMap { messages ->
      messages.toSingleDefault<TextStoryPostSendResult>(TextStoryPostSendResult.Success)
    }
  }

  private fun serializeTextStoryState(textStoryPostCreationState: TextStoryPostCreationState): String {
    val builder = StoryTextPost.newBuilder()

    builder.body = textStoryPostCreationState.body.toString()
    builder.background = textStoryPostCreationState.backgroundColor.serialize()
    builder.style = when (textStoryPostCreationState.textFont) {
      TextFont.REGULAR -> StoryTextPost.Style.REGULAR
      TextFont.BOLD -> StoryTextPost.Style.BOLD
      TextFont.SERIF -> StoryTextPost.Style.SERIF
      TextFont.SCRIPT -> StoryTextPost.Style.SCRIPT
      TextFont.CONDENSED -> StoryTextPost.Style.CONDENSED
    }
    builder.textBackgroundColor = textStoryPostCreationState.textBackgroundColor
    builder.textForegroundColor = textStoryPostCreationState.textForegroundColor

    return Base64.encodeBytes(builder.build().toByteArray())
  }
}
