package org.mycrimes.insecuretests.components.settings.conversation.permissions

import org.mycrimes.insecuretests.groups.ui.GroupChangeFailureReason

sealed class PermissionsSettingsEvents {
  class GroupChangeError(val reason: GroupChangeFailureReason) : PermissionsSettingsEvents()
}
