package org.mycrimes.insecuretests.database

import org.mycrimes.insecuretests.attachments.AttachmentId
import org.mycrimes.insecuretests.attachments.DatabaseAttachment
import org.mycrimes.insecuretests.audio.AudioHash
import org.mycrimes.insecuretests.blurhash.BlurHash
import org.mycrimes.insecuretests.contactshare.Contact
import org.mycrimes.insecuretests.database.documents.IdentityKeyMismatch
import org.mycrimes.insecuretests.database.documents.NetworkFailure
import org.mycrimes.insecuretests.database.model.MediaMmsMessageRecord
import org.mycrimes.insecuretests.database.model.ParentStoryId
import org.mycrimes.insecuretests.database.model.Quote
import org.mycrimes.insecuretests.database.model.ReactionRecord
import org.mycrimes.insecuretests.database.model.StoryType
import org.mycrimes.insecuretests.database.model.databaseprotos.BodyRangeList
import org.mycrimes.insecuretests.database.model.databaseprotos.GiftBadge
import org.mycrimes.insecuretests.linkpreview.LinkPreview
import org.mycrimes.insecuretests.mms.SlideDeck
import org.mycrimes.insecuretests.payments.Payment
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.stickers.StickerLocator
import org.mycrimes.insecuretests.util.MediaUtil

/**
 * Builds MessageRecords and related components for direct usage in unit testing. Does not modify the database.
 */
object FakeMessageRecords {

  fun buildDatabaseAttachment(
    attachmentId: AttachmentId = AttachmentId(1, 1),
    mmsId: Long = 1,
    hasData: Boolean = true,
    hasThumbnail: Boolean = true,
    contentType: String = MediaUtil.IMAGE_JPEG,
    transferProgress: Int = AttachmentTable.TRANSFER_PROGRESS_DONE,
    size: Long = 0L,
    fileName: String = "",
    cdnNumber: Int = 1,
    location: String = "",
    key: String = "",
    relay: String = "",
    digest: ByteArray = byteArrayOf(),
    fastPreflightId: String = "",
    voiceNote: Boolean = false,
    borderless: Boolean = false,
    videoGif: Boolean = false,
    width: Int = 0,
    height: Int = 0,
    quote: Boolean = false,
    caption: String? = null,
    stickerLocator: StickerLocator? = null,
    blurHash: BlurHash? = null,
    audioHash: AudioHash? = null,
    transformProperties: AttachmentTable.TransformProperties? = null,
    displayOrder: Int = 0,
    uploadTimestamp: Long = 200
  ): DatabaseAttachment {
    return DatabaseAttachment(
      attachmentId,
      mmsId,
      hasData,
      hasThumbnail,
      contentType,
      transferProgress,
      size,
      fileName,
      cdnNumber,
      location,
      key,
      relay,
      digest,
      fastPreflightId,
      voiceNote,
      borderless,
      videoGif,
      width,
      height,
      quote,
      caption,
      stickerLocator,
      blurHash,
      audioHash,
      transformProperties,
      displayOrder,
      uploadTimestamp
    )
  }

  fun buildLinkPreview(
    url: String = "",
    title: String = "",
    description: String = "",
    date: Long = 200,
    attachmentId: AttachmentId? = null
  ): LinkPreview {
    return LinkPreview(
      url,
      title,
      description,
      date,
      attachmentId
    )
  }

  fun buildMediaMmsMessageRecord(
    id: Long = 1,
    conversationRecipient: Recipient = Recipient.UNKNOWN,
    individualRecipient: Recipient = conversationRecipient,
    recipientDeviceId: Int = 1,
    dateSent: Long = 200,
    dateReceived: Long = 400,
    dateServer: Long = 300,
    deliveryReceiptCount: Int = 0,
    threadId: Long = 1,
    body: String = "body",
    slideDeck: SlideDeck = SlideDeck(),
    partCount: Int = slideDeck.slides.count(),
    mailbox: Long = MessageTypes.BASE_INBOX_TYPE,
    mismatches: Set<IdentityKeyMismatch> = emptySet(),
    failures: Set<NetworkFailure> = emptySet(),
    subscriptionId: Int = -1,
    expiresIn: Long = -1,
    expireStarted: Long = -1,
    viewOnce: Boolean = false,
    readReceiptCount: Int = 0,
    quote: Quote? = null,
    contacts: List<Contact> = emptyList(),
    linkPreviews: List<LinkPreview> = emptyList(),
    unidentified: Boolean = false,
    reactions: List<ReactionRecord> = emptyList(),
    remoteDelete: Boolean = false,
    mentionsSelf: Boolean = false,
    notifiedTimestamp: Long = 350,
    viewedReceiptCount: Int = 0,
    receiptTimestamp: Long = 0,
    messageRanges: BodyRangeList? = null,
    storyType: StoryType = StoryType.NONE,
    parentStoryId: ParentStoryId? = null,
    giftBadge: GiftBadge? = null,
    payment: Payment? = null,
    call: CallTable.Call? = null
  ): MediaMmsMessageRecord {
    return MediaMmsMessageRecord(
      id,
      conversationRecipient,
      recipientDeviceId,
      individualRecipient,
      dateSent,
      dateReceived,
      dateServer,
      deliveryReceiptCount,
      threadId,
      body,
      slideDeck,
      mailbox,
      mismatches,
      failures,
      subscriptionId,
      expiresIn,
      expireStarted,
      viewOnce,
      readReceiptCount,
      quote,
      contacts,
      linkPreviews,
      unidentified,
      reactions,
      remoteDelete,
      mentionsSelf,
      notifiedTimestamp,
      viewedReceiptCount,
      receiptTimestamp,
      messageRanges,
      storyType,
      parentStoryId,
      giftBadge,
      payment,
      call,
      -1,
      null,
      null,
      0
    )
  }
}
