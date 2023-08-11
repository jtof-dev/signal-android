package org.mycrimes.insecuretests.migrations;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.dependencies.ApplicationDependencies;
import org.mycrimes.insecuretests.jobmanager.Job;
import org.mycrimes.insecuretests.jobmanager.impl.NetworkConstraint;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.recipients.RecipientId;
import org.whispersystems.signalservice.api.push.ACI;

import java.io.IOException;
import java.util.Objects;

/**
 * Couple migrations steps need to happen after we move to UUIDS.
 *  - We need to get our own UUID.
 *  - We need to fetch the new UUID sealed sender cert.
 *  - We need to do a directory sync so we can guarantee that all active users have UUIDs.
 */
public class UuidMigrationJob extends MigrationJob {

  public static final String KEY = "UuidMigrationJob";

  private static final String TAG = Log.tag(UuidMigrationJob.class);

  UuidMigrationJob() {
    this(new Parameters.Builder().addConstraint(NetworkConstraint.KEY).build());
  }

  private UuidMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  boolean isUiBlocking() {
    return false;
  }

  @Override
  void performMigration() throws Exception {
    if (!SignalStore.account().isRegistered() || TextUtils.isEmpty(SignalStore.account().getE164())) {
      Log.w(TAG, "Not registered! Skipping migration, as it wouldn't do anything.");
      return;
    }

    ensureSelfRecipientExists(context);
    fetchOwnUuid(context);
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return e instanceof IOException;
  }

  private static void ensureSelfRecipientExists(@NonNull Context context) {
    SignalDatabase.recipients().getOrInsertFromE164(Objects.requireNonNull(SignalStore.account().getE164()));
  }

  private static void fetchOwnUuid(@NonNull Context context) throws IOException {
    RecipientId self      = Recipient.self().getId();
    ACI         localUuid = ACI.parseOrNull(ApplicationDependencies.getSignalServiceAccountManager().getWhoAmI().getAci());

    if (localUuid == null) {
      throw new IOException("Invalid UUID!");
    }

    SignalDatabase.recipients().markRegisteredOrThrow(self, localUuid);
    SignalStore.account().setAci(localUuid);
  }

  public static class Factory implements Job.Factory<UuidMigrationJob> {
    @Override
    public @NonNull UuidMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new UuidMigrationJob(parameters);
    }
  }
}
