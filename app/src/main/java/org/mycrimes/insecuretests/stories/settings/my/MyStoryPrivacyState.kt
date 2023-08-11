package org.mycrimes.insecuretests.stories.settings.my

import org.mycrimes.insecuretests.database.model.DistributionListPrivacyMode

data class MyStoryPrivacyState(val privacyMode: DistributionListPrivacyMode? = null, val connectionCount: Int = 0)
