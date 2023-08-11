package org.mycrimes.insecuretests.absbackup.backupables

import com.google.protobuf.InvalidProtocolBufferException
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.absbackup.AndroidBackupItem
import org.mycrimes.insecuretests.absbackup.protos.KbsAuthToken
import org.mycrimes.insecuretests.keyvalue.SignalStore

/**
 * This backs up the not-secret KBS Auth tokens, which can be combined with a PIN to prove ownership of a phone number in order to complete the registration process.
 */
object KbsAuthTokens : AndroidBackupItem {
  private const val TAG = "KbsAuthTokens"

  override fun getKey(): String {
    return TAG
  }

  override fun getDataForBackup(): ByteArray {
    val proto = KbsAuthToken(tokens = SignalStore.kbsValues().kbsAuthTokenList)
    return proto.encode()
  }

  override fun restoreData(data: ByteArray) {
    if (SignalStore.kbsValues().kbsAuthTokenList.isNotEmpty()) {
      return
    }

    try {
      val proto = KbsAuthToken.ADAPTER.decode(data)

      SignalStore.kbsValues().putAuthTokenList(proto.tokens)
    } catch (e: InvalidProtocolBufferException) {
      Log.w(TAG, "Cannot restore KbsAuthToken from backup service.")
    }
  }
}
