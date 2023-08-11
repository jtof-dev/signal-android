package org.mycrimes.insecuretests.service.webrtc

import org.signal.ringrtc.CallManager
import org.mycrimes.insecuretests.groups.GroupId
import org.mycrimes.insecuretests.recipients.RecipientId
import java.util.UUID

data class GroupCallRingCheckInfo(
  val recipientId: RecipientId,
  val groupId: GroupId.V2,
  val ringId: Long,
  val ringerUuid: UUID,
  val ringUpdate: CallManager.RingUpdate
)
