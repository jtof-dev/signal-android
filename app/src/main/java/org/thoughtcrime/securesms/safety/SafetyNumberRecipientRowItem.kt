package org.mycrimes.insecuretests.safety

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.signal.core.util.DimensionUnit
import org.signal.core.util.or
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.AvatarImageView
import org.mycrimes.insecuretests.components.menu.ActionItem
import org.mycrimes.insecuretests.components.menu.SignalContextMenu
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.adapter.mapping.MappingModel
import org.mycrimes.insecuretests.util.adapter.mapping.MappingViewHolder
import org.mycrimes.insecuretests.util.visible

/**
 * An untrusted recipient who can be verified or removed.
 */
object SafetyNumberRecipientRowItem {
  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(Model::class.java, LayoutFactory(::ViewHolder, R.layout.safety_number_recipient_row_item))
  }

  class Model(
    val recipient: Recipient,
    val isVerified: Boolean,
    val distributionListMembershipCount: Int,
    val groupMembershipCount: Int,
    val getContextMenuActions: (Model) -> List<ActionItem>
  ) : MappingModel<Model> {
    override fun areItemsTheSame(newItem: Model): Boolean {
      return recipient.id == newItem.recipient.id
    }

    override fun areContentsTheSame(newItem: Model): Boolean {
      return recipient.hasSameContent(newItem.recipient) &&
        isVerified == newItem.isVerified &&
        distributionListMembershipCount == newItem.distributionListMembershipCount &&
        groupMembershipCount == newItem.groupMembershipCount
    }
  }

  class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val avatar: AvatarImageView = itemView.findViewById(R.id.safety_number_recipient_avatar)
    private val name: TextView = itemView.findViewById(R.id.safety_number_recipient_name)
    private val identifier: TextView = itemView.findViewById(R.id.safety_number_recipient_identifier)

    override fun bind(model: Model) {
      avatar.setRecipient(model.recipient)
      name.text = model.recipient.getDisplayName(context)

      val identifierText = model.recipient.e164.or(model.recipient.username).orElse(null)
      val subLineText = when {
        model.isVerified && identifierText.isNullOrBlank() -> context.getString(R.string.SafetyNumberRecipientRowItem__verified)
        model.isVerified -> context.getString(R.string.SafetyNumberRecipientRowItem__s_dot_verified, identifierText)
        else -> identifierText
      }

      identifier.text = subLineText
      identifier.visible = !subLineText.isNullOrBlank()

      itemView.setOnClickListener {
        itemView.isSelected = true
        SignalContextMenu.Builder(itemView, itemView.rootView as ViewGroup)
          .offsetY(DimensionUnit.DP.toPixels(12f).toInt())
          .onDismiss { itemView.isSelected = false }
          .show(model.getContextMenuActions(model))
      }
    }
  }
}
