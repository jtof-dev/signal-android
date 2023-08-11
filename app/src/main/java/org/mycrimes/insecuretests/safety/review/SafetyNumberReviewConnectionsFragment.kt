package org.mycrimes.insecuretests.safety.review

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import org.signal.core.util.concurrent.LifecycleDisposable
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.WrapperDialogFragment
import org.mycrimes.insecuretests.components.menu.ActionItem
import org.mycrimes.insecuretests.components.settings.DSLConfiguration
import org.mycrimes.insecuretests.components.settings.DSLSettingsFragment
import org.mycrimes.insecuretests.components.settings.DSLSettingsText
import org.mycrimes.insecuretests.components.settings.configure
import org.mycrimes.insecuretests.crypto.IdentityKeyParcelable
import org.mycrimes.insecuretests.database.IdentityTable
import org.mycrimes.insecuretests.safety.SafetyNumberBottomSheetState
import org.mycrimes.insecuretests.safety.SafetyNumberBottomSheetViewModel
import org.mycrimes.insecuretests.safety.SafetyNumberBucket
import org.mycrimes.insecuretests.safety.SafetyNumberBucketRowItem
import org.mycrimes.insecuretests.safety.SafetyNumberRecipientRowItem
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.verify.VerifyIdentityFragment

/**
 * Full-screen fragment which displays the list of users who have safety number changes.
 * Consider this an extension of the bottom sheet.
 */
class SafetyNumberReviewConnectionsFragment : DSLSettingsFragment(
  titleId = R.string.SafetyNumberReviewConnectionsFragment__safety_number_changes,
  layoutId = R.layout.safety_number_review_fragment
) {

  private val viewModel: SafetyNumberBottomSheetViewModel by viewModels(ownerProducer = {
    requireParentFragment().requireParentFragment()
  })

  private val lifecycleDisposable = LifecycleDisposable()

  override fun bindAdapter(adapter: MappingAdapter) {
    SafetyNumberBucketRowItem.register(adapter)
    SafetyNumberRecipientRowItem.register(adapter)
    lifecycleDisposable.bindTo(viewLifecycleOwner)

    val done = requireView().findViewById<View>(R.id.done)
    done.setOnClickListener {
      requireActivity().onBackPressed()
    }

    lifecycleDisposable += viewModel.state.subscribe { state ->
      adapter.submitList(getConfiguration(state).toMappingModelList())
    }
  }

  private fun getConfiguration(state: SafetyNumberBottomSheetState): DSLConfiguration {
    return configure {
      val recipientCount = state.destinationToRecipientMap.values.flatten().size
      textPref(
        title = DSLSettingsText.from(
          resources.getQuantityString(R.plurals.SafetyNumberReviewConnectionsFragment__d_recipients_may_have, recipientCount, recipientCount),
          DSLSettingsText.TextAppearanceModifier(R.style.Signal_Text_BodyMedium),
          DSLSettingsText.ColorModifier(ContextCompat.getColor(requireContext(), R.color.signal_colorOnSurfaceVariant))
        )
      )

      state.destinationToRecipientMap.forEach { (bucket, recipients) ->
        customPref(SafetyNumberBucketRowItem.createModel(bucket, this@SafetyNumberReviewConnectionsFragment::getActionItemsForBucket))

        recipients.forEach {
          customPref(
            SafetyNumberRecipientRowItem.Model(
              recipient = it.recipient,
              isVerified = it.identityRecord.verifiedStatus == IdentityTable.VerifiedStatus.VERIFIED,
              distributionListMembershipCount = it.distributionListMembershipCount,
              groupMembershipCount = it.groupMembershipCount,
              getContextMenuActions = { model ->
                val actions = mutableListOf<ActionItem>()

                actions.add(
                  ActionItem(
                    iconRes = R.drawable.ic_safety_number_24,
                    title = getString(R.string.SafetyNumberBottomSheetFragment__verify_safety_number),
                    action = {
                      lifecycleDisposable += viewModel.getIdentityRecord(model.recipient.id).subscribe { record ->
                        VerifyIdentityFragment.createDialog(
                          model.recipient.id,
                          IdentityKeyParcelable(record.identityKey),
                          false
                        ).show(childFragmentManager, null)
                      }
                    }
                  )
                )

                if (model.distributionListMembershipCount > 0) {
                  actions.add(
                    ActionItem(
                      iconRes = R.drawable.ic_circle_x_24,
                      title = getString(R.string.SafetyNumberBottomSheetFragment__remove_from_story),
                      action = {
                        viewModel.removeRecipientFromSelectedStories(model.recipient.id)
                      }
                    )
                  )
                }

                if (model.distributionListMembershipCount == 0 && model.groupMembershipCount == 0) {
                  actions.add(
                    ActionItem(
                      iconRes = R.drawable.ic_circle_x_24,
                      title = getString(R.string.SafetyNumberReviewConnectionsFragment__remove),
                      tintRes = R.color.signal_colorOnSurface,
                      action = {
                        viewModel.removeDestination(model.recipient.id)
                      }
                    )
                  )
                }

                actions
              }
            )
          )
        }
      }
    }
  }

  private fun getActionItemsForBucket(bucket: SafetyNumberBucket): List<ActionItem> {
    return when (bucket) {
      is SafetyNumberBucket.DistributionListBucket -> {
        listOf(
          ActionItem(
            iconRes = R.drawable.ic_circle_x_24,
            title = getString(R.string.SafetyNumberReviewConnectionsFragment__remove_all),
            tintRes = R.color.signal_colorOnSurface,
            action = {
              viewModel.removeAll(bucket)
            }
          )
        )
      }
      else -> emptyList()
    }
  }

  class Dialog : WrapperDialogFragment() {
    override fun getWrappedFragment(): Fragment {
      return SafetyNumberReviewConnectionsFragment()
    }
  }

  companion object {
    fun show(fragmentManager: FragmentManager) {
      Dialog().show(fragmentManager, null)
    }
  }
}
