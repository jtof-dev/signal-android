package org.mycrimes.insecuretests.search

import org.mycrimes.insecuretests.recipients.Recipient

data class ContactSearchResult(val results: List<Recipient>, val query: String)
