package org.mycrimes.insecuretests.sharing;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter;

public class ShareSelectionAdapter extends MappingAdapter {
  public ShareSelectionAdapter() {
    registerFactory(ShareSelectionMappingModel.class,
                    ShareSelectionViewHolder.createFactory(R.layout.share_contact_selection_item));
  }
}
