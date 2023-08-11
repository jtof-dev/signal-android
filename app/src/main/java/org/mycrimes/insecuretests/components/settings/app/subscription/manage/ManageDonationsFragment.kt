package org.mycrimes.insecuretests.components.settings.app.subscription.manage

import android.content.Intent
import android.text.SpannableStringBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.signal.core.util.dp
import org.signal.core.util.money.FiatMoney
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.badges.gifts.ExpiredGiftSheet
import org.mycrimes.insecuretests.badges.gifts.flow.GiftFlowActivity
import org.mycrimes.insecuretests.badges.models.BadgePreview
import org.mycrimes.insecuretests.components.settings.DSLConfiguration
import org.mycrimes.insecuretests.components.settings.DSLSettingsFragment
import org.mycrimes.insecuretests.components.settings.DSLSettingsIcon
import org.mycrimes.insecuretests.components.settings.DSLSettingsText
import org.mycrimes.insecuretests.components.settings.app.AppSettingsActivity
import org.mycrimes.insecuretests.components.settings.app.subscription.MonthlyDonationRepository
import org.mycrimes.insecuretests.components.settings.app.subscription.donate.DonateToSignalType
import org.mycrimes.insecuretests.components.settings.app.subscription.models.NetworkFailure
import org.mycrimes.insecuretests.components.settings.configure
import org.mycrimes.insecuretests.components.settings.models.IndeterminateLoadingCircle
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.help.HelpFragment
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.subscription.Subscription
import org.mycrimes.insecuretests.util.Material3OnScrollHelper
import org.mycrimes.insecuretests.util.SpanUtil
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.navigation.safeNavigate
import org.whispersystems.signalservice.api.subscriptions.ActiveSubscription
import java.util.Currency
import java.util.concurrent.TimeUnit

/**
 * Fragment displayed when a user enters "Subscriptions" via app settings but is already
 * a subscriber. Used to manage their current subscription, view badges, and boost.
 */
class ManageDonationsFragment :
  DSLSettingsFragment(
    layoutId = R.layout.manage_donations_fragment
  ),
  ExpiredGiftSheet.Callback {

  private val supportTechSummary: CharSequence by lazy {
    SpannableStringBuilder(SpanUtil.color(ContextCompat.getColor(requireContext(), R.color.signal_colorOnSurfaceVariant), requireContext().getString(R.string.DonateToSignalFragment__private_messaging)))
      .append(" ")
      .append(
        SpanUtil.readMore(requireContext(), ContextCompat.getColor(requireContext(), R.color.signal_colorPrimary)) {
          findNavController().safeNavigate(ManageDonationsFragmentDirections.actionManageDonationsFragmentToSubscribeLearnMoreBottomSheetDialog())
        }
      )
  }

  private val viewModel: ManageDonationsViewModel by viewModels(
    factoryProducer = {
      ManageDonationsViewModel.Factory(MonthlyDonationRepository(ApplicationDependencies.getDonationsService()))
    }
  )

  override fun onResume() {
    super.onResume()
    viewModel.refresh()
  }

  override fun bindAdapter(adapter: MappingAdapter) {
    ActiveSubscriptionPreference.register(adapter)
    IndeterminateLoadingCircle.register(adapter)
    BadgePreview.register(adapter)
    NetworkFailure.register(adapter)

    val expiredGiftBadge = SignalStore.donationsValues().getExpiredGiftBadge()
    if (expiredGiftBadge != null) {
      SignalStore.donationsValues().setExpiredGiftBadge(null)
      ExpiredGiftSheet.show(childFragmentManager, expiredGiftBadge)
    }

    viewModel.state.observe(viewLifecycleOwner) { state ->
      adapter.submitList(getConfiguration(state).toMappingModelList())
    }
  }

  override fun getMaterial3OnScrollHelper(toolbar: Toolbar?): Material3OnScrollHelper {
    return object : Material3OnScrollHelper(requireActivity(), toolbar!!) {
      override val activeColorSet: ColorSet = ColorSet(R.color.transparent, R.color.signal_colorBackground)
      override val inactiveColorSet: ColorSet = ColorSet(R.color.transparent, R.color.signal_colorBackground)
    }
  }

  private fun getConfiguration(state: ManageDonationsState): DSLConfiguration {
    return configure {
      space(36.dp)

      customPref(
        BadgePreview.BadgeModel.SubscriptionModel(
          badge = state.featuredBadge
        )
      )

      space(12.dp)

      noPadTextPref(
        title = DSLSettingsText.from(
          R.string.DonateToSignalFragment__privacy_over_profit,
          DSLSettingsText.CenterModifier,
          DSLSettingsText.TitleLargeModifier
        )
      )

      space(8.dp)

      noPadTextPref(
        title = DSLSettingsText.from(supportTechSummary, DSLSettingsText.CenterModifier)
      )

      space(24.dp)

      primaryWrappedButton(
        text = DSLSettingsText.from(R.string.ManageDonationsFragment__donate_to_signal),
        onClick = {
          findNavController().safeNavigate(ManageDonationsFragmentDirections.actionManageDonationsFragmentToDonateToSignalFragment(DonateToSignalType.ONE_TIME))
        }
      )

      space(16.dp)

      if (state.transactionState is ManageDonationsState.TransactionState.NotInTransaction) {
        val activeSubscription = state.transactionState.activeSubscription.activeSubscription
        if (activeSubscription != null) {
          val subscription: Subscription? = state.availableSubscriptions.firstOrNull { it.level == activeSubscription.level }
          if (subscription != null) {
            presentSubscriptionSettings(activeSubscription, subscription, state.getMonthlyDonorRedemptionState())
          } else {
            customPref(IndeterminateLoadingCircle)
          }
        } else if (state.hasOneTimeBadge) {
          presentActiveOneTimeDonorSettings()
        } else {
          presentNotADonorSettings(state.hasReceipts)
        }
      } else if (state.transactionState == ManageDonationsState.TransactionState.NetworkFailure) {
        presentNetworkFailureSettings(state.getMonthlyDonorRedemptionState(), state.hasReceipts)
      } else {
        customPref(IndeterminateLoadingCircle)
      }
    }
  }

  private fun DSLConfiguration.presentActiveOneTimeDonorSettings() {
    dividerPref()

    sectionHeaderPref(R.string.ManageDonationsFragment__my_support)

    presentBadges()

    presentOtherWaysToGive()

    presentMore()
  }

  private fun DSLConfiguration.presentNetworkFailureSettings(redemptionState: ManageDonationsState.SubscriptionRedemptionState, hasReceipts: Boolean) {
    if (SignalStore.donationsValues().isLikelyASustainer()) {
      presentSubscriptionSettingsWithNetworkError(redemptionState)
    } else {
      presentNotADonorSettings(hasReceipts)
    }
  }

  private fun DSLConfiguration.presentSubscriptionSettingsWithNetworkError(redemptionState: ManageDonationsState.SubscriptionRedemptionState) {
    presentSubscriptionSettingsWithState(redemptionState) {
      customPref(
        NetworkFailure.Model(
          onRetryClick = {
            viewModel.retry()
          }
        )
      )
    }
  }

  private fun DSLConfiguration.presentSubscriptionSettings(
    activeSubscription: ActiveSubscription.Subscription,
    subscription: Subscription,
    redemptionState: ManageDonationsState.SubscriptionRedemptionState
  ) {
    presentSubscriptionSettingsWithState(redemptionState) {
      val activeCurrency = Currency.getInstance(activeSubscription.currency)
      val activeAmount = activeSubscription.amount.movePointLeft(activeCurrency.defaultFractionDigits)

      customPref(
        ActiveSubscriptionPreference.Model(
          price = FiatMoney(activeAmount, activeCurrency),
          subscription = subscription,
          renewalTimestamp = TimeUnit.SECONDS.toMillis(activeSubscription.endOfCurrentPeriod),
          redemptionState = redemptionState,
          onContactSupport = {
            requireActivity().finish()
            requireActivity().startActivity(AppSettingsActivity.help(requireContext(), HelpFragment.DONATION_INDEX))
          },
          activeSubscription = activeSubscription
        )
      )
    }
  }

  private fun DSLConfiguration.presentSubscriptionSettingsWithState(
    redemptionState: ManageDonationsState.SubscriptionRedemptionState,
    subscriptionBlock: DSLConfiguration.() -> Unit
  ) {
    dividerPref()

    sectionHeaderPref(R.string.ManageDonationsFragment__my_support)

    subscriptionBlock()

    clickPref(
      title = DSLSettingsText.from(R.string.ManageDonationsFragment__manage_subscription),
      icon = DSLSettingsIcon.from(R.drawable.symbol_person_24),
      isEnabled = redemptionState != ManageDonationsState.SubscriptionRedemptionState.IN_PROGRESS,
      onClick = {
        findNavController().safeNavigate(ManageDonationsFragmentDirections.actionManageDonationsFragmentToDonateToSignalFragment(DonateToSignalType.MONTHLY))
      }
    )

    presentBadges()

    presentOtherWaysToGive()

    presentMore()
  }

  private fun DSLConfiguration.presentNotADonorSettings(hasReceipts: Boolean) {
    presentOtherWaysToGive()

    if (hasReceipts) {
      presentMore()
    }
  }

  private fun DSLConfiguration.presentOtherWaysToGive() {
    dividerPref()

    sectionHeaderPref(R.string.ManageDonationsFragment__other_ways_to_give)

    if (Recipient.self().giftBadgesCapability == Recipient.Capability.SUPPORTED) {
      clickPref(
        title = DSLSettingsText.from(R.string.ManageDonationsFragment__donate_for_a_friend),
        icon = DSLSettingsIcon.from(R.drawable.symbol_gift_24),
        onClick = {
          startActivity(Intent(requireContext(), GiftFlowActivity::class.java))
        }
      )
    }
  }

  private fun DSLConfiguration.presentBadges() {
    clickPref(
      title = DSLSettingsText.from(R.string.ManageDonationsFragment__badges),
      icon = DSLSettingsIcon.from(R.drawable.symbol_badge_multi_24),
      onClick = {
        findNavController().safeNavigate(ManageDonationsFragmentDirections.actionManageDonationsFragmentToManageBadges())
      }
    )
  }

  private fun DSLConfiguration.presentReceipts() {
    clickPref(
      title = DSLSettingsText.from(R.string.ManageDonationsFragment__donation_receipts),
      icon = DSLSettingsIcon.from(R.drawable.symbol_receipt_24),
      onClick = {
        findNavController().safeNavigate(ManageDonationsFragmentDirections.actionManageDonationsFragmentToDonationReceiptListFragment())
      }
    )
  }

  private fun DSLConfiguration.presentMore() {
    dividerPref()

    sectionHeaderPref(R.string.ManageDonationsFragment__more)

    presentReceipts()

    externalLinkPref(
      title = DSLSettingsText.from(R.string.ManageDonationsFragment__subscription_faq),
      icon = DSLSettingsIcon.from(R.drawable.symbol_help_24),
      linkId = R.string.donate_url
    )
  }

  override fun onMakeAMonthlyDonation() {
    findNavController().safeNavigate(ManageDonationsFragmentDirections.actionManageDonationsFragmentToDonateToSignalFragment(DonateToSignalType.MONTHLY))
  }
}