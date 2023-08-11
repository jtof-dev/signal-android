package org.mycrimes.insecuretests.stories.viewer.post

import android.graphics.Typeface
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.model.MmsMessageRecord
import org.mycrimes.insecuretests.database.model.databaseprotos.StoryTextPost
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.fonts.TextFont
import org.mycrimes.insecuretests.fonts.TextToScript
import org.mycrimes.insecuretests.fonts.TypefaceCache
import org.mycrimes.insecuretests.util.Base64

class StoryTextPostRepository {
  fun getRecord(recordId: Long): Single<MmsMessageRecord> {
    return Single.fromCallable {
      SignalDatabase.messages.getMessageRecord(recordId) as MmsMessageRecord
    }.subscribeOn(Schedulers.io())
  }

  fun getTypeface(recordId: Long): Single<Typeface> {
    return getRecord(recordId).flatMap {
      val model = StoryTextPost.parseFrom(Base64.decode(it.body))
      val textFont = TextFont.fromStyle(model.style)
      val script = TextToScript.guessScript(model.body)

      TypefaceCache.get(ApplicationDependencies.getApplication(), textFont, script)
    }
  }
}
