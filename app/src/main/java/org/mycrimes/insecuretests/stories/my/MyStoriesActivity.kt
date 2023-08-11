package org.mycrimes.insecuretests.stories.my

import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.components.FragmentWrapperActivity

class MyStoriesActivity : FragmentWrapperActivity() {
  override fun getFragment(): Fragment {
    return MyStoriesFragment()
  }
}
