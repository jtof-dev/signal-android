/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.mycrimes.insecuretests.calls.links.create

import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.service.webrtc.links.CreateCallLinkResult

sealed interface EnsureCallLinkCreatedResult {
  data class Success(val recipient: Recipient) : EnsureCallLinkCreatedResult
  data class Failure(val failure: CreateCallLinkResult.Failure) : EnsureCallLinkCreatedResult
}
