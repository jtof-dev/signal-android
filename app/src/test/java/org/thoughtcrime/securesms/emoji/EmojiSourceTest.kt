package org.mycrimes.insecuretests.emoji

import android.net.Uri
import org.junit.Assert
import org.junit.Test
import org.mycrimes.insecuretests.components.emoji.Emoji
import org.mycrimes.insecuretests.components.emoji.EmojiPageModel

class EmojiSourceTest {

  @Test
  fun `Given a bunch of data pages with max value 100100, when I get the maxEmojiLength, then I expect 6`() {
    val emojiDataFake = ParsedEmojiData(EmojiMetrics(-1, -1, -1), listOf(), "png", listOf(), dataPages = generatePages(), emptyMap(), listOf())
    val testSubject = EmojiSource(0f, emojiDataFake) { uri -> EmojiPage.Disk(uri) }

    Assert.assertEquals(6, testSubject.maxEmojiLength)
  }

  private fun generatePages() = (1..10).map { EmojiPageModelFake((1..100).shuffled().map { Emoji("$it$it") }) }

  private class EmojiPageModelFake(private val displayE: List<Emoji>) : EmojiPageModel {

    override fun getKey(): String = TODO("Not yet implemented")

    override fun getEmoji(): List<String> = displayE.map { it.variations }.flatten()

    override fun getDisplayEmoji(): List<Emoji> = displayE

    override fun getIconAttr(): Int = TODO("Not yet implemented")

    override fun getSpriteUri(): Uri = TODO("Not yet implemented")

    override fun isDynamic(): Boolean = TODO("Not yet implemented")
  }
}
