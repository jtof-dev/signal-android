package org.mycrimes.insecuretests.components.settings.app.subscription.donate.paypal

import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.adapter.mapping.MappingModel
import org.mycrimes.insecuretests.util.adapter.mapping.MappingViewHolder.SimpleViewHolder

/**
 * Line item on the PayPal order confirmation screen.
 */
object PayPalCompleteOrderPaymentItem {
  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(Model::class.java, LayoutFactory(::SimpleViewHolder, R.layout.paypal_complete_order_payment_item))
  }

  class Model : MappingModel<Model> {
    override fun areItemsTheSame(newItem: Model): Boolean = true

    override fun areContentsTheSame(newItem: Model): Boolean = true
  }
}
