package org.mycrimes.insecuretests.components.settings.conversation

import org.mycrimes.insecuretests.util.DynamicNoActionBarTheme
import org.mycrimes.insecuretests.util.DynamicTheme

class CallInfoActivity : ConversationSettingsActivity(), ConversationSettingsFragment.Callback {

  override val dynamicTheme: DynamicTheme = DynamicNoActionBarTheme()
}
