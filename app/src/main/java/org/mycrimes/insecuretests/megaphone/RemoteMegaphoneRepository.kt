package org.mycrimes.insecuretests.megaphone

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import org.json.JSONArray
import org.json.JSONException
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.components.settings.app.subscription.InAppDonations
import org.mycrimes.insecuretests.components.settings.app.subscription.donate.DonateToSignalActivity
import org.mycrimes.insecuretests.database.RemoteMegaphoneTable
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.RemoteMegaphoneRecord
import org.mycrimes.insecuretests.database.model.RemoteMegaphoneRecord.ActionId
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.megaphone.RemoteMegaphoneRepository.Action
import org.mycrimes.insecuretests.providers.BlobProvider
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.util.FeatureFlags
import org.mycrimes.insecuretests.util.LocaleFeatureFlags
import org.mycrimes.insecuretests.util.VersionTracker
import java.util.Objects
import kotlin.math.min
import kotlin.time.Duration.Companion.days

/**
 * Access point for interacting with Remote Megaphones.
 */
object RemoteMegaphoneRepository {

  private val TAG = Log.tag(RemoteMegaphoneRepository::class.java)

  private val db: RemoteMegaphoneTable = SignalDatabase.remoteMegaphones
  private val context: Application = ApplicationDependencies.getApplication()

  private val snooze: Action = Action { _, controller, remote ->
    controller.onMegaphoneSnooze(Megaphones.Event.REMOTE_MEGAPHONE)
    db.snooze(remote)
  }

  private val finish: Action = Action { context, controller, remote ->
    if (remote.imageUri != null) {
      BlobProvider.getInstance().delete(context, remote.imageUri)
    }
    controller.onMegaphoneSnooze(Megaphones.Event.REMOTE_MEGAPHONE)
    db.markFinished(remote.uuid)
  }

  private val donate: Action = Action { context, controller, remote ->
    controller.onMegaphoneNavigationRequested(Intent(context, DonateToSignalActivity::class.java))
    snooze.run(context, controller, remote)
  }

  private val actions = mapOf(
    ActionId.SNOOZE.id to snooze,
    ActionId.FINISH.id to finish,
    ActionId.DONATE.id to donate
  )

  @WorkerThread
  @JvmStatic
  fun hasRemoteMegaphoneToShow(canShowLocalDonate: Boolean): Boolean {
    val record = getRemoteMegaphoneToShow()

    return if (record == null) {
      false
    } else if (record.primaryActionId?.isDonateAction == true) {
      canShowLocalDonate
    } else {
      true
    }
  }

  @WorkerThread
  @JvmStatic
  fun getRemoteMegaphoneToShow(now: Long = System.currentTimeMillis()): RemoteMegaphoneRecord? {
    return db.getPotentialMegaphonesAndClearOld(now)
      .asSequence()
      .filter { it.imageUrl == null || it.imageUri != null }
      .filter { it.countries == null || LocaleFeatureFlags.shouldShowReleaseNote(it.uuid, it.countries) }
      .filter { it.conditionalId == null || checkCondition(it.conditionalId) }
      .filter { it.snoozedAt == 0L || checkSnooze(it, now) }
      .firstOrNull()
  }

  @AnyThread
  @JvmStatic
  fun getAction(action: ActionId): Action {
    return actions[action.id] ?: finish
  }

  @AnyThread
  @JvmStatic
  fun markShown(uuid: String) {
    SignalExecutors.BOUNDED_IO.execute {
      db.markShown(uuid)
    }
  }

  private fun checkCondition(conditionalId: String): Boolean {
    return when (conditionalId) {
      "standard_donate" -> shouldShowDonateMegaphone()
      "internal_user" -> FeatureFlags.internalUser()
      else -> false
    }
  }

  private fun checkSnooze(record: RemoteMegaphoneRecord, now: Long): Boolean {
    if (record.seenCount == 0) {
      return true
    }

    val gaps: JSONArray? = record.getDataForAction(ActionId.SNOOZE)?.getJSONArray("snoozeDurationDays")?.takeIf { it.length() > 0 }
    val gapDays: Int? = gaps?.getIntOrNull(record.seenCount - 1)

    return gapDays == null || (record.snoozedAt + gapDays.days.inWholeMilliseconds <= now)
  }

  private fun shouldShowDonateMegaphone(): Boolean {
    return VersionTracker.getDaysSinceFirstInstalled(context) >= 7 &&
      InAppDonations.hasAtLeastOnePaymentMethodAvailable() &&
      Recipient.self()
        .badges
        .stream()
        .filter { obj: Badge? -> Objects.nonNull(obj) }
        .noneMatch { (_, category): Badge -> category === Badge.Category.Donor }
  }

  fun interface Action {
    fun run(context: Context, controller: MegaphoneActionController, remoteMegaphone: RemoteMegaphoneRecord)
  }

  /**
   * Gets the int at the specified index, or last index of array if larger then array length, or null if unable to parse json
   */
  private fun JSONArray.getIntOrNull(index: Int): Int? {
    return try {
      getInt(min(index, length() - 1))
    } catch (e: JSONException) {
      Log.w(TAG, "Unable to parse", e)
      null
    }
  }
}
