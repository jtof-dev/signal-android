package org.mycrimes.insecuretests.stories.viewer.reply.group

import org.signal.paging.PagedDataSource
import org.mycrimes.insecuretests.conversation.ConversationMessage
import org.mycrimes.insecuretests.database.MessageTable
import org.mycrimes.insecuretests.database.MessageTypes
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.MessageId
import org.mycrimes.insecuretests.database.model.MmsMessageRecord
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.recipients.Recipient

class StoryGroupReplyDataSource(private val parentStoryId: Long) : PagedDataSource<MessageId, ReplyBody> {
  override fun size(): Int {
    return SignalDatabase.messages.getNumberOfStoryReplies(parentStoryId)
  }

  override fun load(start: Int, length: Int, totalSize: Int, cancellationSignal: PagedDataSource.CancellationSignal): MutableList<ReplyBody> {
    val results: MutableList<ReplyBody> = ArrayList(length)
    SignalDatabase.messages.getStoryReplies(parentStoryId).use { cursor ->
      cursor.moveToPosition(start - 1)
      val mmsReader = MessageTable.MmsReader(cursor)
      while (cursor.moveToNext() && cursor.position < start + length) {
        results.add(readRowFromRecord(mmsReader.getCurrent() as MmsMessageRecord))
      }
    }

    return results
  }

  override fun load(key: MessageId): ReplyBody {
    return readRowFromRecord(SignalDatabase.messages.getMessageRecord(key.id) as MmsMessageRecord)
  }

  override fun getKey(data: ReplyBody): MessageId {
    return data.key
  }

  private fun readRowFromRecord(record: MmsMessageRecord): ReplyBody {
    val threadRecipient: Recipient = requireNotNull(SignalDatabase.threads.getRecipientForThreadId(record.threadId))
    return when {
      record.isRemoteDelete -> ReplyBody.RemoteDelete(record)
      MessageTypes.isStoryReaction(record.type) -> ReplyBody.Reaction(record)
      else -> ReplyBody.Text(
        ConversationMessage.ConversationMessageFactory.createWithUnresolvedData(ApplicationDependencies.getApplication(), record, threadRecipient)
      )
    }
  }
}
