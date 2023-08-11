package org.mycrimes.insecuretests.wallpaper;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter;

class ChatWallpaperPreviewAdapter extends MappingAdapter {
  ChatWallpaperPreviewAdapter() {
    registerFactory(ChatWallpaperSelectionMappingModel.class, ChatWallpaperViewHolder.createFactory(R.layout.chat_wallpaper_preview_fragment_adapter_item, null, null));
  }
}
