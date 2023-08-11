package org.mycrimes.insecuretests.wallpaper

import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.AvatarImageView
import org.mycrimes.insecuretests.conversation.colors.AvatarColor
import org.mycrimes.insecuretests.recipients.Recipient

sealed class WallpaperPreviewPortrait {
  class ContactPhoto(private val recipient: Recipient) : WallpaperPreviewPortrait() {
    override fun applyToAvatarImageView(avatarImageView: AvatarImageView) {
      avatarImageView.setAvatar(recipient)
      avatarImageView.colorFilter = null
    }
  }

  class SolidColor(private val avatarColor: AvatarColor) : WallpaperPreviewPortrait() {
    override fun applyToAvatarImageView(avatarImageView: AvatarImageView) {
      avatarImageView.setImageResource(R.drawable.circle_tintable)
      avatarImageView.setColorFilter(avatarColor.colorInt())
    }
  }

  abstract fun applyToAvatarImageView(avatarImageView: AvatarImageView)
}
