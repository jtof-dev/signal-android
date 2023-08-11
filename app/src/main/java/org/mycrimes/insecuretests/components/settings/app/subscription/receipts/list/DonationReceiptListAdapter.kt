package org.mycrimes.insecuretests.components.settings.app.subscription.receipts.list

import android.view.LayoutInflater
import android.view.ViewGroup
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.settings.DSLSettingsText
import org.mycrimes.insecuretests.components.settings.SectionHeaderPreference
import org.mycrimes.insecuretests.components.settings.SectionHeaderPreferenceViewHolder
import org.mycrimes.insecuretests.components.settings.TextPreference
import org.mycrimes.insecuretests.components.settings.TextPreferenceViewHolder
import org.mycrimes.insecuretests.util.StickyHeaderDecoration
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter
import org.mycrimes.insecuretests.util.toLocalDateTime

class DonationReceiptListAdapter(onModelClick: (DonationReceiptListItem.Model) -> Unit) : MappingAdapter(), StickyHeaderDecoration.StickyHeaderAdapter<SectionHeaderPreferenceViewHolder> {

  init {
    registerFactory(TextPreference::class.java, LayoutFactory({ TextPreferenceViewHolder(it) }, R.layout.dsl_preference_item))
    DonationReceiptListItem.register(this, onModelClick)
  }

  override fun getHeaderId(position: Int): Long {
    return when (val item = getItem(position)) {
      is DonationReceiptListItem.Model -> item.record.timestamp.toLocalDateTime().year.toLong()
      else -> StickyHeaderDecoration.StickyHeaderAdapter.NO_HEADER_ID
    }
  }

  override fun onCreateHeaderViewHolder(parent: ViewGroup?, position: Int, type: Int): SectionHeaderPreferenceViewHolder {
    return SectionHeaderPreferenceViewHolder(LayoutInflater.from(parent!!.context).inflate(R.layout.dsl_section_header, parent, false))
  }

  override fun onBindHeaderViewHolder(viewHolder: SectionHeaderPreferenceViewHolder?, position: Int, type: Int) {
    viewHolder?.bind(SectionHeaderPreference(DSLSettingsText.from(getHeaderId(position).toString())))
  }
}
