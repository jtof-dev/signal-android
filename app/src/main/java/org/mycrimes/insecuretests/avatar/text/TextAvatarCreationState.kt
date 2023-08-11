package org.mycrimes.insecuretests.avatar.text

import org.mycrimes.insecuretests.avatar.Avatar
import org.mycrimes.insecuretests.avatar.AvatarColorItem
import org.mycrimes.insecuretests.avatar.Avatars

data class TextAvatarCreationState(
  val currentAvatar: Avatar.Text
) {
  fun colors(): List<AvatarColorItem> = Avatars.colors.map { AvatarColorItem(it, currentAvatar.color == it) }
}
