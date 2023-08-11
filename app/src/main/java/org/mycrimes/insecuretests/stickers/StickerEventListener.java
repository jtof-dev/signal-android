package org.mycrimes.insecuretests.stickers;

import androidx.annotation.NonNull;

import org.mycrimes.insecuretests.database.model.StickerRecord;

public interface StickerEventListener {
  void onStickerSelected(@NonNull StickerRecord sticker);

  void onStickerManagementClicked();
}
