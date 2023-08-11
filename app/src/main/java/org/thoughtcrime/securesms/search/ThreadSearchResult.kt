package org.mycrimes.insecuretests.search

import org.mycrimes.insecuretests.database.model.ThreadRecord

data class ThreadSearchResult(val results: List<ThreadRecord>, val query: String)
