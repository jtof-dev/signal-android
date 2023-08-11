package org.mycrimes.insecuretests.conversation.mutiselect.forward

import org.mycrimes.insecuretests.contacts.paged.ContactSearchKey
import org.mycrimes.insecuretests.database.model.IdentityRecord
import org.mycrimes.insecuretests.stories.Stories

data class MultiselectForwardState(
  val stage: Stage = Stage.Selection,
  val storySendRequirements: Stories.MediaTransform.SendRequirements = Stories.MediaTransform.SendRequirements.CAN_NOT_SEND
) {

  sealed class Stage {
    object Selection : Stage()
    object FirstConfirmation : Stage()
    object LoadingIdentities : Stage()
    data class SafetyConfirmation(val identities: List<IdentityRecord>, val selectedContacts: List<ContactSearchKey>) : Stage()
    object SendPending : Stage()
    object SomeFailed : Stage()
    object AllFailed : Stage()
    object Success : Stage()
    data class SelectionConfirmed(val selectedContacts: Set<ContactSearchKey>) : Stage()
  }
}
