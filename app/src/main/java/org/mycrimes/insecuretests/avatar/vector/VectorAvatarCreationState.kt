package org.mycrimes.insecuretests.avatar.vector

import org.mycrimes.insecuretests.avatar.Avatar
import org.mycrimes.insecuretests.avatar.AvatarColorItem
import org.mycrimes.insecuretests.avatar.Avatars

data class VectorAvatarCreationState(
  val currentAvatar: Avatar.Vector
) {
  fun colors(): List<AvatarColorItem> = Avatars.colors.map { AvatarColorItem(it, currentAvatar.color == it) }
}
