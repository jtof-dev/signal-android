package org.mycrimes.insecuretests.components.reminder

import org.mycrimes.insecuretests.R

class BubbleOptOutReminder : Reminder(R.string.BubbleOptOutTooltip__description) {

  init {
    addAction(Action(R.string.BubbleOptOutTooltip__turn_off, R.id.reminder_action_bubble_turn_off))
    addAction(Action(R.string.BubbleOptOutTooltip__not_now, R.id.reminder_action_bubble_not_now))
  }

  override fun isDismissable(): Boolean {
    return false
  }
}
