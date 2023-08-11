package org.mycrimes.insecuretests.stories.settings.story

import org.mycrimes.insecuretests.contacts.paged.ContactSearchData

data class StoriesPrivacySettingsState(
  val areStoriesEnabled: Boolean,
  val areViewReceiptsEnabled: Boolean,
  val isUpdatingEnabledState: Boolean = false,
  val storyContactItems: List<ContactSearchData> = emptyList(),
  val userHasStories: Boolean = false
)
