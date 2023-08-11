package org.mycrimes.insecuretests.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.preferences.BackupsPreferenceFragment

class WrappedBackupsPreferenceFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.setTitle(R.string.BackupsPreferenceFragment__chat_backups)
    return BackupsPreferenceFragment()
  }
}
