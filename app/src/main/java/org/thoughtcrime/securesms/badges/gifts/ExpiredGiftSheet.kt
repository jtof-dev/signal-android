package org.mycrimes.insecuretests.badges.gifts

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import org.signal.core.util.getParcelableCompat
import org.mycrimes.insecuretests.badges.gifts.ExpiredGiftSheetConfiguration.forExpiredBadge
import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.components.settings.DSLSettingsAdapter
import org.mycrimes.insecuretests.components.settings.DSLSettingsBottomSheetFragment
import org.mycrimes.insecuretests.components.settings.configure
import org.mycrimes.insecuretests.util.BottomSheetUtil
import org.mycrimes.insecuretests.util.fragments.requireListener

/**
 * Displays expired gift information and gives the user the option to start a recurring monthly donation.
 */
class ExpiredGiftSheet : DSLSettingsBottomSheetFragment() {

  companion object {
    private const val ARG_BADGE = "arg.badge"

    fun show(fragmentManager: FragmentManager, badge: Badge) {
      ExpiredGiftSheet().apply {
        arguments = Bundle().apply {
          putParcelable(ARG_BADGE, badge)
        }
      }.show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }

  private val badge: Badge
    get() = requireArguments().getParcelableCompat(ARG_BADGE, Badge::class.java)!!

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    ExpiredGiftSheetConfiguration.register(adapter)
    adapter.submitList(
      configure {
        forExpiredBadge(
          badge = badge,
          onMakeAMonthlyDonation = {
            requireListener<Callback>().onMakeAMonthlyDonation()
          },
          onNotNow = {
            dismissAllowingStateLoss()
          }
        )
      }.toMappingModelList()
    )
  }

  interface Callback {
    fun onMakeAMonthlyDonation()
  }
}
