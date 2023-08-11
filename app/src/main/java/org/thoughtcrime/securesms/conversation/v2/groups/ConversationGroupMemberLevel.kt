package org.mycrimes.insecuretests.conversation.v2.groups

import org.mycrimes.insecuretests.database.GroupTable

/**
 * @param groupTableMemberLevel Self membership level
 * @param isAnnouncementGroup   Whether the group is an announcement group.
 */
data class ConversationGroupMemberLevel(
  val groupTableMemberLevel: GroupTable.MemberLevel,
  val isAnnouncementGroup: Boolean
)
