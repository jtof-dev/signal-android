package org.mycrimes.insecuretests

import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.AndroidLogger
import org.signal.core.util.logging.Log
import org.signal.libsignal.protocol.logging.SignalProtocolLoggerProvider
import org.mycrimes.insecuretests.database.LogDatabase
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies
import org.mycrimes.insecuretests.dependencies.ApplicationDependencyProvider
import org.mycrimes.insecuretests.dependencies.InstrumentationApplicationDependencyProvider
import org.mycrimes.insecuretests.logging.CustomSignalProtocolLogger
import org.mycrimes.insecuretests.logging.PersistentLogger
import org.mycrimes.insecuretests.testing.InMemoryLogger

/**
 * Application context for running instrumentation tests (aka androidTests).
 */
class SignalInstrumentationApplicationContext : ApplicationContext() {

  val inMemoryLogger: InMemoryLogger = InMemoryLogger()

  override fun initializeAppDependencies() {
    val default = ApplicationDependencyProvider(this)
    ApplicationDependencies.init(this, InstrumentationApplicationDependencyProvider(this, default))
    ApplicationDependencies.getDeadlockDetector().start()
  }

  override fun initializeLogging() {
    persistentLogger = PersistentLogger(this)

    Log.initialize({ true }, AndroidLogger(), persistentLogger, inMemoryLogger)

    SignalProtocolLoggerProvider.setProvider(CustomSignalProtocolLogger())

    SignalExecutors.UNBOUNDED.execute {
      Log.blockUntilAllWritesFinished()
      LogDatabase.getInstance(this).trimToSize()
    }
  }
}
