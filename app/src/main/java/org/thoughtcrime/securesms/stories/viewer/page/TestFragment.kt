package org.mycrimes.insecuretests.stories.viewer.page

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.conversation.colors.AvatarColor

class TestFragment : Fragment(R.layout.test_fragment) {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    (view as AppCompatImageView).setImageDrawable(ColorDrawable(AvatarColor.random().colorInt()))
  }
}
