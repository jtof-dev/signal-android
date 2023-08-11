package org.mycrimes.insecuretests.profiles.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.ViewBinderDelegate
import org.mycrimes.insecuretests.databinding.UsernameEducationFragmentBinding
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.megaphone.Megaphones
import org.mycrimes.insecuretests.util.CommunicationActions
import org.mycrimes.insecuretests.util.navigation.safeNavigate

/**
 * Displays a Username education screen which displays some basic information
 * about usernames and provides a learn-more link.
 */
class UsernameEducationFragment : Fragment(R.layout.username_education_fragment) {
  private val binding by ViewBinderDelegate(UsernameEducationFragmentBinding::bind)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.toolbar.setNavigationOnClickListener {
      findNavController().popBackStack()
    }

    binding.usernameEducationLearnMore.setOnClickListener {
      CommunicationActions.openBrowserLink(requireContext(), getString(R.string.username_support_url))
    }

    binding.continueButton.setOnClickListener {
      SignalStore.uiHints().markHasSeenUsernameEducation()
      ApplicationDependencies.getMegaphoneRepository().markFinished(Megaphones.Event.SET_UP_YOUR_USERNAME)
      findNavController().safeNavigate(UsernameEducationFragmentDirections.actionUsernameEducationFragmentToUsernameManageFragment())
    }
  }
}
