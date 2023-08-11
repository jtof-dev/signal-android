package org.mycrimes.insecuretests.components.settings.app.privacy.expire

import android.content.Context
import androidx.annotation.WorkerThread
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.ThreadTable
import org.mycrimes.insecuretests.groups.GroupChangeException
import org.mycrimes.insecuretests.groups.GroupManager
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.mms.OutgoingMessage
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.sms.MessageSender
import org.mycrimes.insecuretests.storage.StorageSyncHelper
import java.io.IOException

private val TAG: String = Log.tag(ExpireTimerSettingsRepository::class.java)

/**
 * Provide operations to set expire timer for individuals and groups.
 */
class ExpireTimerSettingsRepository(val context: Context) {

  fun setExpiration(recipientId: RecipientId, newExpirationTime: Int, consumer: (Result<Int>) -> Unit) {
    SignalExecutors.BOUNDED.execute {
      val recipient = Recipient.resolved(recipientId)
      if (recipient.groupId.isPresent && recipient.groupId.get().isPush) {
        try {
          GroupManager.updateGroupTimer(context, recipient.groupId.get().requirePush(), newExpirationTime)
          consumer.invoke(Result.success(newExpirationTime))
        } catch (e: GroupChangeException) {
          Log.w(TAG, e)
          consumer.invoke(Result.failure(e))
        } catch (e: IOException) {
          Log.w(TAG, e)
          consumer.invoke(Result.failure(e))
        }
      } else {
        SignalDatabase.recipients.setExpireMessages(recipientId, newExpirationTime)
        val outgoingMessage = OutgoingMessage.expirationUpdateMessage(Recipient.resolved(recipientId), System.currentTimeMillis(), newExpirationTime * 1000L)
        MessageSender.send(context, outgoingMessage, getThreadId(recipientId), MessageSender.SendType.SIGNAL, null, null)
        consumer.invoke(Result.success(newExpirationTime))
      }
    }
  }

  fun setUniversalExpireTimerSeconds(newExpirationTime: Int, onDone: () -> Unit) {
    SignalExecutors.BOUNDED.execute {
      SignalStore.settings().universalExpireTimer = newExpirationTime
      SignalDatabase.recipients.markNeedsSync(Recipient.self().id)
      StorageSyncHelper.scheduleSyncForDataChange()
      onDone.invoke()
    }
  }

  @WorkerThread
  private fun getThreadId(recipientId: RecipientId): Long {
    val threadTable: ThreadTable = SignalDatabase.threads
    val recipient: Recipient = Recipient.resolved(recipientId)
    return threadTable.getOrCreateThreadIdFor(recipient)
  }
}
