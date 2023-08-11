package org.mycrimes.insecuretests.components

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import org.mycrimes.insecuretests.R

/**
 * Displays a small progress spinner in a card view, as a non-cancellable dialog fragment.
 */
class ProgressCardDialogFragment : DialogFragment(R.layout.progress_card_dialog) {

  private val args: ProgressCardDialogFragmentArgs by navArgs()

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    isCancelable = false
    return super.onCreateDialog(savedInstanceState).apply {
      this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    view.findViewById<ProgressCard>(R.id.progress_card).setTitleText(args.title)
  }
}