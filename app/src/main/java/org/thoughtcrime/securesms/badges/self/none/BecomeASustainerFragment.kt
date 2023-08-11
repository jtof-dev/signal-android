package org.mycrimes.insecuretests.badges.self.none

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import org.signal.core.util.DimensionUnit
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.badges.models.BadgePreview
import org.mycrimes.insecuretests.components.settings.DSLConfiguration
import org.mycrimes.insecuretests.components.settings.DSLSettingsAdapter
import org.mycrimes.insecuretests.components.settings.DSLSettingsBottomSheetFragment
import org.mycrimes.insecuretests.components.settings.DSLSettingsText
import org.mycrimes.insecuretests.components.settings.app.AppSettingsActivity
import org.mycrimes.insecuretests.components.settings.app.subscription.MonthlyDonationRepository
import org.mycrimes.insecuretests.components.settings.configure
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.util.BottomSheetUtil

class BecomeASustainerFragment : DSLSettingsBottomSheetFragment() {

  private val viewModel: BecomeASustainerViewModel by viewModels(
    factoryProducer = {
      BecomeASustainerViewModel.Factory(MonthlyDonationRepository(ApplicationDependencies.getDonationsService()))
    }
  )

  override fun bindAdapter(adapter: DSLSettingsAdapter) {
    BadgePreview.register(adapter)

    viewModel.state.observe(viewLifecycleOwner) {
      adapter.submitList(getConfiguration(it).toMappingModelList())
    }
  }

  private fun getConfiguration(state: BecomeASustainerState): DSLConfiguration {
    return configure {
      customPref(BadgePreview.BadgeModel.FeaturedModel(badge = state.badge))

      sectionHeaderPref(
        title = DSLSettingsText.from(
          R.string.BecomeASustainerFragment__get_badges,
          DSLSettingsText.CenterModifier,
          DSLSettingsText.TitleLargeModifier
        )
      )

      space(DimensionUnit.DP.toPixels(8f).toInt())

      noPadTextPref(
        title = DSLSettingsText.from(
          R.string.BecomeASustainerFragment__signal_is_a_non_profit,
          DSLSettingsText.CenterModifier,
          DSLSettingsText.TextAppearanceModifier(R.style.Signal_Text_BodyMedium),
          DSLSettingsText.ColorModifier(ContextCompat.getColor(requireContext(), R.color.signal_colorOnSurfaceVariant))
        )
      )

      space(DimensionUnit.DP.toPixels(32f).toInt())

      tonalButton(
        text = DSLSettingsText.from(
          R.string.BecomeASustainerMegaphone__become_a_sustainer
        ),
        onClick = {
          requireActivity().finish()
          requireActivity().startActivity(AppSettingsActivity.subscriptions(requireContext()).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
      )

      space(DimensionUnit.DP.toPixels(32f).toInt())
    }
  }

  companion object {
    @JvmStatic
    fun show(fragmentManager: FragmentManager) {
      BecomeASustainerFragment().show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }
}
