package org.mycrimes.insecuretests.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.preferences.MmsPreferencesFragment

class WrappedMmsPreferencesFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.setTitle(R.string.preferences__advanced_mms_access_point_names)
    return MmsPreferencesFragment()
  }
}
