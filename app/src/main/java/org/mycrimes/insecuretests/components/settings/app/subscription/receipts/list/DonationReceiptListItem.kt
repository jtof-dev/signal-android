package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.list

import android.view.View
import android.widget.TextView
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.badges.BadgeImageView
import org.mycrimes.insecuretests.badges.models.Badge
import org.mycrimes.insecuretests.database.model.DonationReceiptRecord
import org.mycrimes.insecuretests.payments.FiatMoneyUtil
import org.mycrimes.insecuretests.util.DateUtils
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.adapter.mapping.MappingModel
import org.mycrimes.insecuretests.util.adapter.mapping.MappingViewHolder
import java.util.Locale

object DonationReceiptListItem {

  fun register(adapter: MappingAdapter, onClick: (Model) -> Unit) {
    adapter.registerFactory(Model::class.java, LayoutFactory({ ViewHolder(it, onClick) }, R.layout.donation_receipt_list_item))
  }

  class Model(
    val record: DonationReceiptRecord,
    val badge: Badge?
  ) : MappingModel<Model> {
    override fun areContentsTheSame(newItem: Model): Boolean = record == newItem.record && badge == newItem.badge

    override fun areItemsTheSame(newItem: Model): Boolean = record.id == newItem.record.id
  }

  private class ViewHolder(itemView: View, private val onClick: (Model) -> Unit) : MappingViewHolder<Model>(itemView) {

    private val badgeView: BadgeImageView = itemView.findViewById(R.id.badge)
    private val dateView: TextView = itemView.findViewById(R.id.date)
    private val typeView: TextView = itemView.findViewById(R.id.type)
    private val moneyView: TextView = itemView.findViewById(R.id.money)

    override fun bind(model: Model) {
      itemView.setOnClickListener { onClick(model) }
      badgeView.setBadge(model.badge)
      dateView.text = DateUtils.formatDate(Locale.getDefault(), model.record.timestamp)
      typeView.setText(
        when (model.record.type) {
          DonationReceiptRecord.Type.RECURRING -> R.string.DonationReceiptListFragment__recurring
          DonationReceiptRecord.Type.BOOST -> R.string.DonationReceiptListFragment__one_time
          DonationReceiptRecord.Type.GIFT -> R.string.DonationReceiptListFragment__donation_for_a_friend
        }
      )
      moneyView.text = FiatMoneyUtil.format(context.resources, model.record.amount)
    }
  }
}
