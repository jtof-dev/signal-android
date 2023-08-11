package org.mycrimes.insecuretests.components.settings.app.notifications.profiles.models

import android.view.View
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.settings.DSLSettingsIcon
import org.mycrimes.insecuretests.components.settings.DSLSettingsText
import org.mycrimes.insecuretests.components.settings.NO_TINT
import org.mycrimes.insecuretests.components.settings.PreferenceModel
import org.mycrimes.insecuretests.components.settings.PreferenceViewHolder
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter

/**
 * Custom DSL preference for adding members to a profile.
 */
object NotificationProfileAddMembers {

  fun register(adapter: MappingAdapter) {
    adapter.registerFactory(Model::class.java, LayoutFactory(::ViewHolder, R.layout.large_icon_preference_item))
  }

  class Model(
    override val title: DSLSettingsText = DSLSettingsText.from(R.string.AddAllowedMembers__add_people_or_groups),
    override val icon: DSLSettingsIcon = DSLSettingsIcon.from(R.drawable.add_to_a_group, NO_TINT),
    val onClick: (Long, Set<RecipientId>) -> Unit,
    val profileId: Long,
    val currentSelection: Set<RecipientId>
  ) : PreferenceModel<Model>() {
    override fun areContentsTheSame(newItem: Model): Boolean {
      return super.areContentsTheSame(newItem) && profileId == newItem.profileId && currentSelection == newItem.currentSelection
    }
  }

  private class ViewHolder(itemView: View) : PreferenceViewHolder<Model>(itemView) {
    override fun bind(model: Model) {
      super.bind(model)
      itemView.setOnClickListener { model.onClick(model.profileId, model.currentSelection) }
    }
  }
}
