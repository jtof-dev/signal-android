package org.mycrimes.insecuretests.badges.models

import android.view.View
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.badges.BadgeImageView
import org.mycrimes.insecuretests.components.settings.PreferenceModel
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.adapter.mapping.MappingViewHolder

object ExpiredBadge {

  class Model(val badge: Badge) : PreferenceModel<Model>() {
    override fun areItemsTheSame(newItem: Model): Boolean {
      return newItem.badge.id == badge.id
    }

    override fun areContentsTheSame(newItem: Model): Boolean {
      return super.areContentsTheSame(newItem) && newItem.badge == badge
    }
  }

  class ViewHolder(itemView: View) : MappingViewHolder<Model>(itemView) {

    private val badge: BadgeImageView = itemView.findViewById(R.id.expired_badge)

    override fun bind(model: Model) {
      badge.setBadge(model.badge)
    }
  }

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, LayoutFactory({ ViewHolder(it) }, R.layout.expired_badge_preference))
  }
}
