package org.mycrimes.insecuretests.components.settings.models

import android.view.View
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.settings.PreferenceModel
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.adapter.mapping.MappingViewHolder

object IndeterminateLoadingCircle : PreferenceModel<IndeterminateLoadingCircle>() {
  override fun areItemsTheSame(newItem: IndeterminateLoadingCircle): Boolean = true

  private class ViewHolder(itemView: View) : MappingViewHolder<IndeterminateLoadingCircle>(itemView) {
    override fun bind(model: IndeterminateLoadingCircle) = Unit
  }

  fun register(mappingAdapter: MappingAdapter) {
    mappingAdapter.registerFactory(IndeterminateLoadingCircle::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.indeterminate_loading_circle_pref))
  }
}
