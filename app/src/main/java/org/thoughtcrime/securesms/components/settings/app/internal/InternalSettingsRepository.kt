package org.mycrimes.insecuretests.components.settings.app.internal

import android.content.Context
import org.signal.core.util.concurrent.SignalExecutors
import org.mycrimes.insecuretests.database.MessageTable
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.addStyle
import org.mycrimes.insecuretests.database.model.databaseprotos.BodyRangeList
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.emoji.EmojiFiles
import org.mycrimes.insecuretests.jobs.AttachmentDownloadJob
import org.mycrimes.insecuretests.jobs.CreateReleaseChannelJob
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.notifications.v2.ConversationId
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.releasechannel.ReleaseChannel

class InternalSettingsRepository(context: Context) {

  private val context = context.applicationContext

  fun getEmojiVersionInfo(consumer: (EmojiFiles.Version?) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      consumer(EmojiFiles.Version.readVersion(context))
    }
  }

  fun addSampleReleaseNote() {
    SignalExecutors.UNBOUNDED.execute {
      ApplicationDependencies.getJobManager().runSynchronously(CreateReleaseChannelJob.create(), 5000)

      val title = "Release Note Title"
      val bodyText = "Release note body. Aren't I awesome?"
      val body = "$title\n\n$bodyText"
      val bodyRangeList = BodyRangeList.newBuilder()
        .addStyle(BodyRangeList.BodyRange.Style.BOLD, 0, title.length)

      val recipientId = SignalStore.releaseChannelValues().releaseChannelRecipientId!!
      val threadId = SignalDatabase.threads.getOrCreateThreadIdFor(Recipient.resolved(recipientId))

      val insertResult: MessageTable.InsertResult? = ReleaseChannel.insertReleaseChannelMessage(
        recipientId = recipientId,
        body = body,
        threadId = threadId,
        messageRanges = bodyRangeList.build(),
        media = "/static/release-notes/signal.png",
        mediaWidth = 1800,
        mediaHeight = 720
      )

      SignalDatabase.messages.insertBoostRequestMessage(recipientId, threadId)

      if (insertResult != null) {
        SignalDatabase.attachments.getAttachmentsForMessage(insertResult.messageId)
          .forEach { ApplicationDependencies.getJobManager().add(AttachmentDownloadJob(insertResult.messageId, it.attachmentId, false)) }

        ApplicationDependencies.getMessageNotifier().updateNotification(context, ConversationId.forConversation(insertResult.threadId))
      }
    }
  }
}
