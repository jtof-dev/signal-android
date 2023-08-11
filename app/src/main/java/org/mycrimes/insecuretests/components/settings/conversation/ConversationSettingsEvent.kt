package org.mycrimes.insecuretests.components.settings.conversation

import org.mycrimes.insecuretests.groups.GroupId
import org.mycrimes.insecuretests.groups.ui.GroupChangeFailureReason
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId

sealed class ConversationSettingsEvent {
  class AddToAGroup(
    val recipientId: RecipientId,
    val groupMembership: List<RecipientId>
  ) : ConversationSettingsEvent()

  class AddMembersToGroup(
    val groupId: GroupId,
    val selectionWarning: Int,
    val selectionLimit: Int,
    val isAnnouncementGroup: Boolean,
    val groupMembersWithoutSelf: List<RecipientId>
  ) : ConversationSettingsEvent()

  object ShowGroupHardLimitDialog : ConversationSettingsEvent()

  class ShowAddMembersToGroupError(
    val failureReason: GroupChangeFailureReason
  ) : ConversationSettingsEvent()

  class ShowGroupInvitesSentDialog(
    val invitesSentTo: List<Recipient>
  ) : ConversationSettingsEvent()

  class ShowMembersAdded(
    val membersAddedCount: Int
  ) : ConversationSettingsEvent()

  class InitiateGroupMigration(
    val recipientId: RecipientId
  ) : ConversationSettingsEvent()
}
