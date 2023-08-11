package org.mycrimes.insecuretests.database.helpers.migration

import android.app.Application
import net.zetetic.database.sqlcipher.SQLiteDatabase

/**
 * Simple interface for allowing database migrations to live outside of [org.mycrimes.insecuretests.database.helpers.SignalDatabaseMigrations].
 */
interface SignalDatabaseMigration {
  fun migrate(context: Application, db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
}
