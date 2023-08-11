package org.mycrimes.insecuretests.stories.settings.select

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.CursorUtil
import org.signal.core.util.concurrent.SignalExecutors
import org.mycrimes.insecuretests.database.RecipientTable
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.database.model.DistributionListRecord
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.stories.Stories

class BaseStoryRecipientSelectionRepository {

  fun getRecord(distributionListId: DistributionListId): Single<DistributionListRecord> {
    return Single.fromCallable {
      SignalDatabase.distributionLists.getList(distributionListId) ?: error("Record does not exist.")
    }.subscribeOn(Schedulers.io())
  }

  fun updateDistributionListMembership(distributionListRecord: DistributionListRecord, recipients: Set<RecipientId>) {
    SignalExecutors.BOUNDED.execute {
      val currentRecipients = SignalDatabase.distributionLists.getRawMembers(distributionListRecord.id, distributionListRecord.privacyMode).toSet()
      val oldNotNew = currentRecipients - recipients
      val newNotOld = recipients - currentRecipients

      oldNotNew.forEach {
        SignalDatabase.distributionLists.removeMemberFromList(distributionListRecord.id, distributionListRecord.privacyMode, it)
      }

      newNotOld.forEach {
        SignalDatabase.distributionLists.addMemberToList(distributionListRecord.id, distributionListRecord.privacyMode, it)
      }

      Stories.onStorySettingsChanged(distributionListRecord.id)
    }
  }

  fun getAllSignalContacts(): Single<Set<RecipientId>> {
    return Single.fromCallable {
      SignalDatabase.recipients.getSignalContacts(false)?.use {
        val recipientSet = mutableSetOf<RecipientId>()
        while (it.moveToNext()) {
          recipientSet.add(RecipientId.from(CursorUtil.requireLong(it, RecipientTable.ID)))
        }

        recipientSet
      } ?: emptySet()
    }.subscribeOn(Schedulers.io())
  }
}
