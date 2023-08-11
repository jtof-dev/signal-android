package org.mycrimes.insecuretests

import android.content.ContentValues
import android.os.Build
import org.signal.spinner.Spinner
import org.signal.spinner.Spinner.DatabaseConfig
import org.mycrimes.insecuretests.database.DatabaseMonitor
import org.mycrimes.insecuretests.database.GV2Transformer
import org.mycrimes.insecuretests.database.GV2UpdateTransformer
import org.mycrimes.insecuretests.database.IsStoryTransformer
import org.mycrimes.insecuretests.database.JobDatabase
import org.mycrimes.insecuretests.database.KeyValueDatabase
import org.mycrimes.insecuretests.database.LocalMetricsDatabase
import org.mycrimes.insecuretests.database.LogDatabase
import org.mycrimes.insecuretests.database.MegaphoneDatabase
import org.mycrimes.insecuretests.database.MessageBitmaskColumnTransformer
import org.mycrimes.insecuretests.database.MessageRangesTransformer
import org.mycrimes.insecuretests.database.ProfileKeyCredentialTransformer
import org.mycrimes.insecuretests.database.QueryMonitor
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.database.TimestampTransformer
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.util.AppSignatureUtil
import java.util.Locale

class SpinnerApplicationContext : ApplicationContext() {
  override fun onCreate() {
    super.onCreate()

    try {
      Class.forName("dalvik.system.CloseGuard")
        .getMethod("setEnabled", Boolean::class.javaPrimitiveType)
        .invoke(null, true)
    } catch (e: ReflectiveOperationException) {
      throw RuntimeException(e)
    }

    Spinner.init(
      this,
      mapOf(
        "Device" to { "${Build.MODEL} (Android ${Build.VERSION.RELEASE}, API ${Build.VERSION.SDK_INT})" },
        "Package" to { "$packageName (${AppSignatureUtil.getAppSignature(this)})" },
        "App Version" to { "${BuildConfig.VERSION_NAME} (${BuildConfig.CANONICAL_VERSION_CODE}, ${BuildConfig.GIT_HASH})" },
        "Profile Name" to { (if (SignalStore.account().isRegistered) Recipient.self().profileName.toString() else "none") },
        "E164" to { SignalStore.account().e164 ?: "none" },
        "ACI" to { SignalStore.account().aci?.toString() ?: "none" },
        "PNI" to { SignalStore.account().pni?.toString() ?: "none" },
        Spinner.KEY_ENVIRONMENT to { BuildConfig.FLAVOR_environment.uppercase(Locale.US) }
      ),
      linkedMapOf(
        "signal" to DatabaseConfig(
          db = { SignalDatabase.rawDatabase },
          columnTransformers = listOf(MessageBitmaskColumnTransformer, GV2Transformer, GV2UpdateTransformer, IsStoryTransformer, TimestampTransformer, ProfileKeyCredentialTransformer, MessageRangesTransformer)
        ),
        "jobmanager" to DatabaseConfig(db = { JobDatabase.getInstance(this).sqlCipherDatabase }),
        "keyvalue" to DatabaseConfig(db = { KeyValueDatabase.getInstance(this).sqlCipherDatabase }),
        "megaphones" to DatabaseConfig(db = { MegaphoneDatabase.getInstance(this).sqlCipherDatabase }),
        "localmetrics" to DatabaseConfig(db = { LocalMetricsDatabase.getInstance(this).sqlCipherDatabase }),
        "logs" to DatabaseConfig(db = { LogDatabase.getInstance(this).sqlCipherDatabase })
      ),
      linkedMapOf(
        StorageServicePlugin.PATH to StorageServicePlugin()
      )
    )

    DatabaseMonitor.initialize(object : QueryMonitor {
      override fun onSql(sql: String, args: Array<Any>?) {
        Spinner.onSql("signal", sql, args)
      }

      override fun onQuery(distinct: Boolean, table: String, projection: Array<String>?, selection: String?, args: Array<Any>?, groupBy: String?, having: String?, orderBy: String?, limit: String?) {
        Spinner.onQuery("signal", distinct, table, projection, selection, args, groupBy, having, orderBy, limit)
      }

      override fun onDelete(table: String, selection: String?, args: Array<Any>?) {
        Spinner.onDelete("signal", table, selection, args)
      }

      override fun onUpdate(table: String, values: ContentValues, selection: String?, args: Array<Any>?) {
        Spinner.onUpdate("signal", table, values, selection, args)
      }
    })
  }
}
