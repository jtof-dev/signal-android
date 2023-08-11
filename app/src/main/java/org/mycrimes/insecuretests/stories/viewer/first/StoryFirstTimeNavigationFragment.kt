package org.mycrimes.insecuretests.stories.viewer.first

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.signal.core.util.concurrent.LifecycleDisposable
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.stories.StoryFirstTimeNavigationView
import org.mycrimes.insecuretests.stories.viewer.StoryViewerState
import org.mycrimes.insecuretests.stories.viewer.StoryViewerViewModel

class StoryFirstTimeNavigationFragment : DialogFragment(R.layout.story_viewer_first_time_navigation_stub), StoryFirstTimeNavigationView.Callback {

  private val viewModel: StoryViewerViewModel by viewModels(ownerProducer = {
    requireParentFragment()
  })

  private val disposables = LifecycleDisposable()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_FRAME, R.style.Signal_DayNight_Dialog_FullScreen)
    isCancelable = false
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = super.onCreateDialog(savedInstanceState)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dialog.window!!.addFlags(
      WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION or
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
    return dialog
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    (view as StoryFirstTimeNavigationView).show()
    view.callback = this
    viewModel.setIsDisplayingFirstTimeNavigation(true)

    disposables.bindTo(viewLifecycleOwner)
    disposables += viewModel.state.observeOn(AndroidSchedulers.mainThread()).subscribe { state ->
      when (state.crossfadeSource) {
        is StoryViewerState.CrossfadeSource.ImageUri -> {
          view.setBlurHash(state.crossfadeSource.imageBlur)
        }
        else -> {
          view.setBlurHash(null)
        }
      }
    }
  }

  override fun userHasSeenFirstNavigationView(): Boolean {
    return SignalStore.storyValues().userHasSeenFirstNavView
  }

  override fun onGotItClicked() {
    dismissAllowingStateLoss()

    SignalStore.storyValues().userHasSeenFirstNavView = true
    viewModel.setIsDisplayingFirstTimeNavigation(false)
  }

  override fun onCloseClicked() {
    dismissAllowingStateLoss()

    if (viewModel.stateSnapshot.skipCrossfade) {
      requireActivity().finish()
    } else {
      ActivityCompat.finishAfterTransition(requireActivity())
    }
  }
}
