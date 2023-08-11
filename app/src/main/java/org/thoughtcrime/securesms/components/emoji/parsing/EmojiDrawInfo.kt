package org.mycrimes.insecuretests.components.emoji.parsing

import org.mycrimes.insecuretests.emoji.EmojiPage

data class EmojiDrawInfo(val page: EmojiPage, val index: Int, private val emoji: String, val rawEmoji: String?, val jumboSheet: String?)
