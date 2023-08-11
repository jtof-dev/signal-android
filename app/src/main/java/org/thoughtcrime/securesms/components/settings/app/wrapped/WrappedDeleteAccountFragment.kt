package org.mycrimes.insecuretests.components.settings.app.wrapped

import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.delete.DeleteAccountFragment

class WrappedDeleteAccountFragment : SettingsWrapperFragment() {
  override fun getFragment(): Fragment {
    toolbar.setTitle(R.string.preferences__delete_account)
    return DeleteAccountFragment()
  }
}
