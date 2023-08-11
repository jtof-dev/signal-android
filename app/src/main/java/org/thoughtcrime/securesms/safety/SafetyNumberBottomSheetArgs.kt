package org.mycrimes.insecuretests.safety

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.mycrimes.insecuretests.contacts.paged.ContactSearchKey
import org.mycrimes.insecuretests.database.model.MessageId
import org.mycrimes.insecuretests.recipients.RecipientId

/**
 * Fragment argument for `SafetyNumberBottomSheetFragment`
 */
@Parcelize
data class SafetyNumberBottomSheetArgs(
  val untrustedRecipients: List<RecipientId>,
  val destinations: List<ContactSearchKey.RecipientSearchKey>,
  val messageId: MessageId? = null
) : Parcelable
