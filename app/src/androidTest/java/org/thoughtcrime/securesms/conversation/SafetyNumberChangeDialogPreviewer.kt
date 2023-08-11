package org.mycrimes.insecuretests.conversation

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mycrimes.insecuretests.contacts.paged.ContactSearchKey
import org.mycrimes.insecuretests.database.IdentityTable
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.DistributionListId
import org.mycrimes.insecuretests.database.model.DistributionListPrivacyMode
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.profiles.ProfileName
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.safety.SafetyNumberBottomSheet
import org.mycrimes.insecuretests.testing.SignalActivityRule

/**
 * Android test to help show SNC dialog quickly with custom data to make sure it displays properly.
 */
@Ignore("For testing/previewing manually, no assertions")
@RunWith(AndroidJUnit4::class)
class SafetyNumberChangeDialogPreviewer {

  @get:Rule val harness = SignalActivityRule(othersCount = 10)

  @Test
  fun testShowLongName() {
    val other: Recipient = Recipient.resolved(harness.others.first())

    SignalDatabase.recipients.setProfileName(other.id, ProfileName.fromParts("Super really long name like omg", "But seriously it's long like really really long"))

    harness.setVerified(other, IdentityTable.VerifiedStatus.VERIFIED)
    harness.changeIdentityKey(other)

    val scenario: ActivityScenario<ConversationActivity> = harness.launchActivity { putExtra("recipient_id", other.id.serialize()) }
    scenario.onActivity {
      SafetyNumberBottomSheet.forRecipientId(other.id).show(it.supportFragmentManager)
    }

    // Uncomment to make dialog stay on screen, otherwise will show/dismiss immediately
    // ThreadUtil.sleep(15000)
  }

  @Test
  fun testShowLargeSheet() {
    SignalDatabase.distributionLists.setPrivacyMode(DistributionListId.MY_STORY, DistributionListPrivacyMode.ONLY_WITH)

    val othersRecipients = harness.others.map { Recipient.resolved(it) }
    othersRecipients.forEach { other ->
      SignalDatabase.recipients.setProfileName(other.id, ProfileName.fromParts("My", "Name"))

      harness.setVerified(other, IdentityTable.VerifiedStatus.DEFAULT)
      harness.changeIdentityKey(other)

      SignalDatabase.distributionLists.addMemberToList(DistributionListId.MY_STORY, DistributionListPrivacyMode.ONLY_WITH, other.id)
    }

    val myStoryRecipientId = SignalDatabase.distributionLists.getRecipientId(DistributionListId.MY_STORY)!!
    val scenario: ActivityScenario<ConversationActivity> = harness.launchActivity { putExtra("recipient_id", harness.others.first().serialize()) }
    scenario.onActivity { conversationActivity ->
      SafetyNumberBottomSheet
        .forIdentityRecordsAndDestinations(
          identityRecords = ApplicationDependencies.getProtocolStore().aci().identities().getIdentityRecords(othersRecipients).identityRecords,
          destinations = listOf(ContactSearchKey.RecipientSearchKey(myStoryRecipientId, true))
        )
        .show(conversationActivity.supportFragmentManager)
    }

    // Uncomment to make dialog stay on screen, otherwise will show/dismiss immediately
    // ThreadUtil.sleep( 30000)
  }
}
