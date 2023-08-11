package org.mycrimes.insecuretests.contacts.management

import android.content.Context
import androidx.annotation.CheckResult
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.jobs.RotateProfileKeyJob
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientUtil

class ContactsManagementRepository(context: Context) {
  private val context = context.applicationContext

  @CheckResult
  fun blockContact(recipient: Recipient): Completable {
    return Completable.fromAction {
      if (recipient.isDistributionList) {
        error("Blocking a distribution list makes no sense")
      } else if (recipient.isGroup) {
        RecipientUtil.block(context, recipient)
      } else {
        RecipientUtil.blockNonGroup(context, recipient)
      }
    }.subscribeOn(Schedulers.io())
  }

  @CheckResult
  fun hideContact(recipient: Recipient): Completable {
    return Completable.fromAction {
      if (recipient.isGroup || recipient.isDistributionList || recipient.isSelf) {
        error("Cannot hide groups, self, or distribution lists.")
      }

      val rotateProfileKey = !recipient.hasGroupsInCommon()
      SignalDatabase.recipients.markHidden(recipient.id, rotateProfileKey)
      if (rotateProfileKey) {
        ApplicationDependencies.getJobManager().add(RotateProfileKeyJob())
      }
    }
  }
}
