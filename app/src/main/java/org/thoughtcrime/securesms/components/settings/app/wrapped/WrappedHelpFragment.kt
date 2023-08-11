package org.mycrimes.insecuretests.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.help.HelpFragment

class WrappedHelpFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.title = getString(R.string.preferences__help)

    val fragment = HelpFragment()
    fragment.arguments = arguments

    return fragment
  }
}
