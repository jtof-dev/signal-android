package org.mycrimes.insecuretests.stories.settings.privacy

import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.stories.settings.my.MyStoryPrivacyState

data class ChooseInitialMyStoryMembershipState(
  val recipientId: RecipientId? = null,
  val privacyState: MyStoryPrivacyState = MyStoryPrivacyState(),
  val allSignalConnectionsCount: Int = 0,
  val hasUserPerformedManualSelection: Boolean = false
)
