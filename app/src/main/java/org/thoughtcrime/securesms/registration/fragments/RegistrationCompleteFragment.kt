package org.mycrimes.insecuretests.registration.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.ActivityNavigator
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.LoggingFragment
import org.mycrimes.insecuretests.MainActivity
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.jobs.MultiDeviceProfileContentUpdateJob
import org.mycrimes.insecuretests.jobs.MultiDeviceProfileKeyUpdateJob
import org.mycrimes.insecuretests.jobs.ProfileUploadJob
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.lock.v2.CreateKbsPinActivity
import org.mycrimes.insecuretests.pin.PinRestoreActivity
import org.mycrimes.insecuretests.profiles.AvatarHelper
import org.mycrimes.insecuretests.profiles.edit.EditProfileActivity
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.registration.RegistrationUtil
import org.mycrimes.insecuretests.registration.viewmodel.RegistrationViewModel

/**
 * [RegistrationCompleteFragment] is not visible to the user, but functions as basically a redirect towards one of:
 * - [PIN Restore flow activity](org.mycrimes.insecuretests.pin.PinRestoreActivity)
 * - [Profile](org.mycrimes.insecuretests.profiles.edit.EditProfileActivity) / [PIN creation](org.mycrimes.insecuretests.lock.v2.CreateKbsPinActivity) flow activities (this class chains the necessary activities together as an intent)
 * - Exit registration flow and progress to conversation list
 */
class RegistrationCompleteFragment : LoggingFragment() {
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_registration_blank, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val activity = requireActivity()
    val viewModel: RegistrationViewModel by viewModels(ownerProducer = { requireActivity() })

    if (SignalStore.misc().hasLinkedDevices) {
      SignalStore.misc().shouldShowLinkedDevicesReminder = viewModel.isReregister
    }

    if (SignalStore.storageService().needsAccountRestore()) {
      Log.i(TAG, "Performing pin restore.")
      activity.startActivity(Intent(activity, PinRestoreActivity::class.java))
    } else {
      val isProfileNameEmpty = Recipient.self().profileName.isEmpty
      val isAvatarEmpty = !AvatarHelper.hasAvatar(activity, Recipient.self().id)
      val needsProfile = isProfileNameEmpty || isAvatarEmpty
      val needsPin = !SignalStore.kbsValues().hasPin() && !viewModel.isReregister

      Log.i(TAG, "Pin restore flow not required. Profile name: $isProfileNameEmpty | Profile avatar: $isAvatarEmpty | Needs PIN: $needsPin")

      if (!needsProfile && !needsPin) {
        ApplicationDependencies.getJobManager()
          .startChain(ProfileUploadJob())
          .then(listOf(MultiDeviceProfileKeyUpdateJob(), MultiDeviceProfileContentUpdateJob()))
          .enqueue()
        RegistrationUtil.maybeMarkRegistrationComplete()
      }

      var startIntent = MainActivity.clearTop(activity)

      if (needsPin) {
        startIntent = chainIntents(CreateKbsPinActivity.getIntentForPinCreate(activity), startIntent)
      }

      if (needsProfile) {
        startIntent = chainIntents(EditProfileActivity.getIntentForUserProfile(activity), startIntent)
      }

      activity.startActivity(startIntent)
    }

    activity.finish()
    ActivityNavigator.applyPopAnimationsToPendingTransition(activity)
  }

  private fun chainIntents(sourceIntent: Intent, nextIntent: Intent): Intent {
    sourceIntent.putExtra("next_intent", nextIntent)
    return sourceIntent
  }

  companion object {
    private val TAG = Log.tag(RegistrationCompleteFragment::class.java)
  }
}
