package org.mycrimes.insecuretests.sharing.interstitial;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter;
import org.mycrimes.insecuretests.util.viewholders.RecipientViewHolder;

class ShareInterstitialSelectionAdapter extends MappingAdapter {
  ShareInterstitialSelectionAdapter() {
    registerFactory(ShareInterstitialMappingModel.class, RecipientViewHolder.createFactory(R.layout.share_contact_selection_item, null));
  }
}
