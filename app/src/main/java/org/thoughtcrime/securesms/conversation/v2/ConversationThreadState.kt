/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.mycrimes.insecuretests.conversation.v2

import org.signal.paging.ObservablePagedData
import org.mycrimes.insecuretests.conversation.ConversationData
import org.mycrimes.insecuretests.conversation.v2.data.ConversationElementKey
import org.mycrimes.insecuretests.util.adapter.mapping.MappingModel

/**
 * Represents the content that will be displayed in the conversation
 * thread (recycler).
 */
class ConversationThreadState(
  val items: ObservablePagedData<ConversationElementKey, MappingModel<*>>,
  val meta: ConversationData
)
