package org.mycrimes.insecuretests.components.settings.app.notifications.profiles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.signal.core.util.concurrent.LifecycleDisposable
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.settings.DSLConfiguration
import org.mycrimes.insecuretests.components.settings.DSLSettingsFragment
import org.mycrimes.insecuretests.components.settings.app.notifications.profiles.models.NotificationProfileAddMembers
import org.mycrimes.insecuretests.components.settings.app.notifications.profiles.models.NotificationProfileRecipient
import org.mycrimes.insecuretests.components.settings.configure
import org.mycrimes.insecuretests.components.settings.conversation.preferences.RecipientPreference
import org.mycrimes.insecuretests.notifications.profiles.NotificationProfile
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.navigation.safeNavigate
import org.mycrimes.insecuretests.util.views.CircularProgressMaterialButton

/**
 * Show and allow addition of recipients to a profile during the create flow.
 */
class AddAllowedMembersFragment : DSLSettingsFragment(layoutId = R.layout.fragment_add_allowed_members) {

  private val viewModel: AddAllowedMembersViewModel by viewModels(factoryProducer = { AddAllowedMembersViewModel.Factory(profileId) })
  private val lifecycleDisposable = LifecycleDisposable()
  private val profileId: Long by lazy { AddAllowedMembersFragmentArgs.fromBundle(requireArguments()).profileId }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleDisposable.bindTo(viewLifecycleOwner.lifecycle)

    view.findViewById<CircularProgressMaterialButton>(R.id.add_allowed_members_profile_next).apply {
      setOnClickListener {
        findNavController().safeNavigate(AddAllowedMembersFragmentDirections.actionAddAllowedMembersFragmentToEditNotificationProfileScheduleFragment(profileId, true))
      }
    }
  }

  override fun bindAdapter(adapter: MappingAdapter) {
    NotificationProfileAddMembers.register(adapter)
    NotificationProfileRecipient.register(adapter)

    lifecycleDisposable += viewModel.getProfile()
      .subscribeBy(
        onNext = { (profile, recipients) ->
          adapter.submitList(getConfiguration(profile, recipients).toMappingModelList())
        }
      )
  }

  private fun getConfiguration(profile: NotificationProfile, recipients: List<Recipient>): DSLConfiguration {
    return configure {
      sectionHeaderPref(R.string.AddAllowedMembers__allowed_notifications)

      customPref(
        NotificationProfileAddMembers.Model(
          onClick = { id, currentSelection ->
            findNavController().safeNavigate(
              AddAllowedMembersFragmentDirections.actionAddAllowedMembersFragmentToSelectRecipientsFragment(id)
                .setCurrentSelection(currentSelection.toTypedArray())
            )
          },
          profileId = profile.id,
          currentSelection = profile.allowedMembers
        )
      )

      for (member in recipients) {
        customPref(
          NotificationProfileRecipient.Model(
            recipientModel = RecipientPreference.Model(
              recipient = member,
              onClick = {}
            ),
            onRemoveClick = { id ->
              lifecycleDisposable += viewModel.removeMember(id)
                .subscribeBy(
                  onSuccess = { removed ->
                    view?.let { view ->
                      Snackbar.make(view, getString(R.string.NotificationProfileDetails__s_removed, removed.getDisplayName(requireContext())), Snackbar.LENGTH_LONG)
                        .setAction(R.string.NotificationProfileDetails__undo) { undoRemove(id) }
                        .show()
                    }
                  }
                )
            }
          )
        )
      }
    }
  }

  private fun undoRemove(id: RecipientId) {
    lifecycleDisposable += viewModel.addMember(id)
      .subscribe()
  }
}
