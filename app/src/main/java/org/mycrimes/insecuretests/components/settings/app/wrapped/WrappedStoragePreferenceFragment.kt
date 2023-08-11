package org.mycrimes.insecuretests.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.preferences.StoragePreferenceFragment

class WrappedStoragePreferenceFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    return StoragePreferenceFragment()
  }
}
