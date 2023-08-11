package org.mycrimes.insecuretests.wallpaper;

import androidx.annotation.Nullable;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter;

class ChatWallpaperSelectionAdapter extends MappingAdapter {
  ChatWallpaperSelectionAdapter(@Nullable ChatWallpaperViewHolder.EventListener eventListener) {
    registerFactory(ChatWallpaperSelectionMappingModel.class, ChatWallpaperViewHolder.createFactory(R.layout.chat_wallpaper_selection_fragment_adapter_item, eventListener, null));
  }
}
