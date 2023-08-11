package org.mycrimes.insecuretests.dependencies;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.signal.core.util.Hex;
import org.signal.core.util.ThreadUtil;
import org.signal.core.util.concurrent.DeadlockDetector;
import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.libsignal.zkgroup.profiles.ClientZkProfileOperations;
import org.signal.libsignal.zkgroup.receipts.ClientZkReceiptOperations;
import org.mycrimes.insecuretests.BuildConfig;
import org.mycrimes.insecuretests.KbsEnclave;
import org.mycrimes.insecuretests.components.TypingStatusRepository;
import org.mycrimes.insecuretests.components.TypingStatusSender;
import org.mycrimes.insecuretests.crypto.ReentrantSessionLock;
import org.mycrimes.insecuretests.crypto.storage.SignalBaseIdentityKeyStore;
import org.mycrimes.insecuretests.crypto.storage.SignalIdentityKeyStore;
import org.mycrimes.insecuretests.crypto.storage.SignalKyberPreKeyStore;
import org.mycrimes.insecuretests.crypto.storage.SignalSenderKeyStore;
import org.mycrimes.insecuretests.crypto.storage.SignalServiceAccountDataStoreImpl;
import org.mycrimes.insecuretests.crypto.storage.SignalServiceDataStoreImpl;
import org.mycrimes.insecuretests.crypto.storage.TextSecurePreKeyStore;
import org.mycrimes.insecuretests.crypto.storage.TextSecureSessionStore;
import org.mycrimes.insecuretests.database.DatabaseObserver;
import org.mycrimes.insecuretests.database.JobDatabase;
import org.mycrimes.insecuretests.database.PendingRetryReceiptCache;
import org.mycrimes.insecuretests.jobmanager.JobManager;
import org.mycrimes.insecuretests.jobmanager.JobMigrator;
import org.mycrimes.insecuretests.jobmanager.impl.FactoryJobPredicate;
import org.mycrimes.insecuretests.jobs.FastJobStorage;
import org.mycrimes.insecuretests.jobs.GroupCallUpdateSendJob;
import org.mycrimes.insecuretests.jobs.JobManagerFactories;
import org.mycrimes.insecuretests.jobs.MarkerJob;
import org.mycrimes.insecuretests.jobs.PreKeysSyncJob;
import org.mycrimes.insecuretests.jobs.PushDecryptMessageJob;
import org.mycrimes.insecuretests.jobs.PushGroupSendJob;
import org.mycrimes.insecuretests.jobs.IndividualSendJob;
import org.mycrimes.insecuretests.jobs.PushProcessMessageJob;
import org.mycrimes.insecuretests.jobs.PushProcessMessageJobV2;
import org.mycrimes.insecuretests.jobs.ReactionSendJob;
import org.mycrimes.insecuretests.jobs.TypingSendJob;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.megaphone.MegaphoneRepository;
import org.mycrimes.insecuretests.messages.BackgroundMessageRetriever;
import org.mycrimes.insecuretests.messages.IncomingMessageObserver;
import org.mycrimes.insecuretests.net.SignalWebSocketHealthMonitor;
import org.mycrimes.insecuretests.notifications.MessageNotifier;
import org.mycrimes.insecuretests.notifications.OptimizedMessageNotifier;
import org.mycrimes.insecuretests.payments.MobileCoinConfig;
import org.mycrimes.insecuretests.payments.Payments;
import org.mycrimes.insecuretests.push.SecurityEventListener;
import org.mycrimes.insecuretests.push.SignalServiceNetworkAccess;
import org.mycrimes.insecuretests.recipients.LiveRecipientCache;
import org.mycrimes.insecuretests.revealable.ViewOnceMessageManager;
import org.mycrimes.insecuretests.service.DeletedCallEventManager;
import org.mycrimes.insecuretests.service.ExpiringMessageManager;
import org.mycrimes.insecuretests.service.ExpiringStoriesManager;
import org.mycrimes.insecuretests.service.PendingRetryReceiptManager;
import org.mycrimes.insecuretests.service.ScheduledMessageManager;
import org.mycrimes.insecuretests.service.TrimThreadsByDateManager;
import org.mycrimes.insecuretests.service.webrtc.SignalCallManager;
import org.mycrimes.insecuretests.shakereport.ShakeToReport;
import org.mycrimes.insecuretests.stories.Stories;
import org.mycrimes.insecuretests.util.AlarmSleepTimer;
import org.mycrimes.insecuretests.util.AppForegroundObserver;
import org.mycrimes.insecuretests.util.ByteUnit;
import org.mycrimes.insecuretests.util.EarlyMessageCache;
import org.mycrimes.insecuretests.util.FeatureFlags;
import org.mycrimes.insecuretests.util.FrameRateTracker;
import org.mycrimes.insecuretests.util.TextSecurePreferences;
import org.mycrimes.insecuretests.video.exo.GiphyMp4Cache;
import org.mycrimes.insecuretests.video.exo.SimpleExoPlayerPool;
import org.mycrimes.insecuretests.webrtc.audio.AudioManagerCompat;
import org.whispersystems.signalservice.api.KeyBackupService;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalWebSocket;
import org.whispersystems.signalservice.api.groupsv2.ClientZkOperations;
import org.whispersystems.signalservice.api.groupsv2.GroupsV2Operations;
import org.whispersystems.signalservice.api.push.ACI;
import org.whispersystems.signalservice.api.push.PNI;
import org.whispersystems.signalservice.api.services.CallLinksService;
import org.whispersystems.signalservice.api.services.DonationsService;
import org.whispersystems.signalservice.api.services.ProfileService;
import org.whispersystems.signalservice.api.util.CredentialsProvider;
import org.whispersystems.signalservice.api.util.SleepTimer;
import org.whispersystems.signalservice.api.util.UptimeSleepTimer;
import org.whispersystems.signalservice.api.websocket.WebSocketFactory;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.signalservice.internal.websocket.WebSocketConnection;

import java.security.KeyStore;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Implementation of {@link ApplicationDependencies.Provider} that provides real app dependencies.
 */
public class ApplicationDependencyProvider implements ApplicationDependencies.Provider {

  private final Application context;

  public ApplicationDependencyProvider(@NonNull Application context) {
    this.context = context;
  }

  private @NonNull ClientZkOperations provideClientZkOperations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return ClientZkOperations.create(signalServiceConfiguration);
  }

  @Override
  public @NonNull GroupsV2Operations provideGroupsV2Operations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return new GroupsV2Operations(provideClientZkOperations(signalServiceConfiguration), FeatureFlags.groupLimits().getHardLimit());
  }

  @Override
  public @NonNull SignalServiceAccountManager provideSignalServiceAccountManager(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return new SignalServiceAccountManager(signalServiceConfiguration,
                                           new DynamicCredentialsProvider(),
                                           BuildConfig.SIGNAL_AGENT,
                                           groupsV2Operations,
                                           FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceMessageSender provideSignalServiceMessageSender(@NonNull SignalWebSocket signalWebSocket, @NonNull SignalServiceDataStore protocolStore, @NonNull SignalServiceConfiguration signalServiceConfiguration) {
      return new SignalServiceMessageSender(signalServiceConfiguration,
                                            new DynamicCredentialsProvider(),
                                            protocolStore,
                                            ReentrantSessionLock.INSTANCE,
                                            BuildConfig.SIGNAL_AGENT,
                                            signalWebSocket,
                                            Optional.of(new SecurityEventListener(context)),
                                            provideGroupsV2Operations(signalServiceConfiguration).getProfileOperations(),
                                            SignalExecutors.newCachedBoundedExecutor("signal-messages", ThreadUtil.PRIORITY_IMPORTANT_BACKGROUND_THREAD, 1, 16, 30),
                                            ByteUnit.KILOBYTES.toBytes(256),
                                            FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceMessageReceiver provideSignalServiceMessageReceiver(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return new SignalServiceMessageReceiver(signalServiceConfiguration,
                                            new DynamicCredentialsProvider(),
                                            BuildConfig.SIGNAL_AGENT,
                                            provideGroupsV2Operations(signalServiceConfiguration).getProfileOperations(),
                                            FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceNetworkAccess provideSignalServiceNetworkAccess() {
    return new SignalServiceNetworkAccess(context);
  }

  @Override
  public @NonNull BackgroundMessageRetriever provideBackgroundMessageRetriever() {
    return new BackgroundMessageRetriever();
  }

  @Override
  public @NonNull LiveRecipientCache provideRecipientCache() {
    return new LiveRecipientCache(context);
  }

  @Override
  public @NonNull JobManager provideJobManager() {
    JobManager.Configuration config = new JobManager.Configuration.Builder()
                                                                  .setJobFactories(JobManagerFactories.getJobFactories(context))
                                                                  .setConstraintFactories(JobManagerFactories.getConstraintFactories(context))
                                                                  .setConstraintObservers(JobManagerFactories.getConstraintObservers(context))
                                                                  .setJobStorage(new FastJobStorage(JobDatabase.getInstance(context)))
                                                                  .setJobMigrator(new JobMigrator(TextSecurePreferences.getJobManagerVersion(context), JobManager.CURRENT_VERSION, JobManagerFactories.getJobMigrations(context)))
                                                                  .addReservedJobRunner(new FactoryJobPredicate(PushDecryptMessageJob.KEY, PushProcessMessageJob.KEY, PushProcessMessageJobV2.KEY, MarkerJob.KEY))
                                                                  .addReservedJobRunner(new FactoryJobPredicate(IndividualSendJob.KEY, PushGroupSendJob.KEY, ReactionSendJob.KEY, TypingSendJob.KEY, GroupCallUpdateSendJob.KEY))
                                                                  .build();
    return new JobManager(context, config);
  }

  @Override
  public @NonNull FrameRateTracker provideFrameRateTracker() {
    return new FrameRateTracker(context);
  }

  @SuppressLint("DiscouragedApi")
  public @NonNull MegaphoneRepository provideMegaphoneRepository() {
    return new MegaphoneRepository(context);
  }

  @Override
  public @NonNull EarlyMessageCache provideEarlyMessageCache() {
    return new EarlyMessageCache();
  }

  @Override
  public @NonNull MessageNotifier provideMessageNotifier() {
    return new OptimizedMessageNotifier(context);
  }

  @Override
  public @NonNull IncomingMessageObserver provideIncomingMessageObserver() {
    return new IncomingMessageObserver(context);
  }

  @Override
  public @NonNull TrimThreadsByDateManager provideTrimThreadsByDateManager() {
    return new TrimThreadsByDateManager(context);
  }

  @Override
  public @NonNull ViewOnceMessageManager provideViewOnceMessageManager() {
    return new ViewOnceMessageManager(context);
  }

  @Override
  public @NonNull ExpiringStoriesManager provideExpiringStoriesManager() {
    return new ExpiringStoriesManager(context);
  }

  @Override
  public @NonNull ExpiringMessageManager provideExpiringMessageManager() {
    return new ExpiringMessageManager(context);
  }

  @Override
  public @NonNull DeletedCallEventManager provideDeletedCallEventManager() {
    return new DeletedCallEventManager(context);
  }

  @Override
  public @NonNull ScheduledMessageManager provideScheduledMessageManager() {
    return new ScheduledMessageManager(context);
  }

  @Override
  public @NonNull TypingStatusRepository provideTypingStatusRepository() {
    return new TypingStatusRepository();
  }

  @Override
  public @NonNull TypingStatusSender provideTypingStatusSender() {
    return new TypingStatusSender();
  }

  @Override
  public @NonNull DatabaseObserver provideDatabaseObserver() {
    return new DatabaseObserver(context);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public @NonNull Payments providePayments(@NonNull SignalServiceAccountManager signalServiceAccountManager) {
    MobileCoinConfig network;

    if      (BuildConfig.MOBILE_COIN_ENVIRONMENT.equals("mainnet")) network = MobileCoinConfig.getMainNet(signalServiceAccountManager);
    else if (BuildConfig.MOBILE_COIN_ENVIRONMENT.equals("testnet")) network = MobileCoinConfig.getTestNet(signalServiceAccountManager);
    else throw new AssertionError("Unknown network " + BuildConfig.MOBILE_COIN_ENVIRONMENT);

    return new Payments(network);
  }

  @Override
  public @NonNull ShakeToReport provideShakeToReport() {
    return new ShakeToReport(context);
  }

  @Override
  public @NonNull AppForegroundObserver provideAppForegroundObserver() {
    return new AppForegroundObserver();
  }

  @Override
  public @NonNull SignalCallManager provideSignalCallManager() {
    return new SignalCallManager(context);
  }

  @Override
  public @NonNull PendingRetryReceiptManager providePendingRetryReceiptManager() {
    return new PendingRetryReceiptManager(context);
  }

  @Override
  public @NonNull PendingRetryReceiptCache providePendingRetryReceiptCache() {
    return new PendingRetryReceiptCache();
  }

  @Override
  public @NonNull SignalWebSocket provideSignalWebSocket(@NonNull Supplier<SignalServiceConfiguration> signalServiceConfigurationSupplier) {
    SleepTimer                   sleepTimer      = !SignalStore.account().isFcmEnabled() || SignalStore.internalValues().isWebsocketModeForced() ? new AlarmSleepTimer(context) : new UptimeSleepTimer() ;
    SignalWebSocketHealthMonitor healthMonitor   = new SignalWebSocketHealthMonitor(context, sleepTimer);
    SignalWebSocket              signalWebSocket = new SignalWebSocket(provideWebSocketFactory(signalServiceConfigurationSupplier, healthMonitor));

    healthMonitor.monitor(signalWebSocket);

    return signalWebSocket;
  }

  @Override
  public @NonNull SignalServiceDataStoreImpl provideProtocolStore() {
    ACI localAci = SignalStore.account().getAci();
    PNI localPni = SignalStore.account().getPni();

    if (localAci == null) {
      throw new IllegalStateException("No ACI set!");
    }

    if (localPni == null) {
      throw new IllegalStateException("No PNI set!");
    }

    boolean needsPreKeyJob = false;

    if (!SignalStore.account().hasAciIdentityKey()) {
      SignalStore.account().generateAciIdentityKeyIfNecessary();
      needsPreKeyJob = true;
    }

    if (!SignalStore.account().hasPniIdentityKey()) {
      SignalStore.account().generatePniIdentityKeyIfNecessary();
      needsPreKeyJob = true;
    }

    if (needsPreKeyJob) {
      PreKeysSyncJob.enqueueIfNeeded();
    }

    SignalBaseIdentityKeyStore baseIdentityStore = new SignalBaseIdentityKeyStore(context);

    SignalServiceAccountDataStoreImpl aciStore = new SignalServiceAccountDataStoreImpl(context,
                                                                                       new TextSecurePreKeyStore(localAci),
                                                                                       new SignalKyberPreKeyStore(localAci),
                                                                                       new SignalIdentityKeyStore(baseIdentityStore, () -> SignalStore.account().getAciIdentityKey()),
                                                                                       new TextSecureSessionStore(localAci),
                                                                                       new SignalSenderKeyStore(context));

    SignalServiceAccountDataStoreImpl pniStore = new SignalServiceAccountDataStoreImpl(context,
                                                                                       new TextSecurePreKeyStore(localPni),
                                                                                       new SignalKyberPreKeyStore(localPni),
                                                                                       new SignalIdentityKeyStore(baseIdentityStore, () -> SignalStore.account().getPniIdentityKey()),
                                                                                       new TextSecureSessionStore(localPni),
                                                                                       new SignalSenderKeyStore(context));
    return new SignalServiceDataStoreImpl(context, aciStore, pniStore);
  }

  @Override
  public @NonNull GiphyMp4Cache provideGiphyMp4Cache() {
    return new GiphyMp4Cache(ByteUnit.MEGABYTES.toBytes(16));
  }

  @Override
  public @NonNull SimpleExoPlayerPool provideExoPlayerPool() {
    return new SimpleExoPlayerPool(context);
  }

  @Override
  public @NonNull AudioManagerCompat provideAndroidCallAudioManager() {
    return AudioManagerCompat.create(context);
  }

  @Override
  public @NonNull DonationsService provideDonationsService(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return new DonationsService(signalServiceConfiguration,
                                new DynamicCredentialsProvider(),
                                BuildConfig.SIGNAL_AGENT,
                                groupsV2Operations,
                                FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull CallLinksService provideCallLinksService(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return new CallLinksService(signalServiceConfiguration,
                                new DynamicCredentialsProvider(),
                                BuildConfig.SIGNAL_AGENT,
                                groupsV2Operations,
                                FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull ProfileService provideProfileService(@NonNull ClientZkProfileOperations clientZkProfileOperations,
                                                       @NonNull SignalServiceMessageReceiver receiver,
                                                       @NonNull SignalWebSocket signalWebSocket)
  {
    return new ProfileService(clientZkProfileOperations, receiver, signalWebSocket);
  }

  @Override
  public @NonNull DeadlockDetector provideDeadlockDetector() {
    HandlerThread handlerThread = new HandlerThread("signal-DeadlockDetector", ThreadUtil.PRIORITY_BACKGROUND_THREAD);
    handlerThread.start();
    return new DeadlockDetector(new Handler(handlerThread.getLooper()), TimeUnit.SECONDS.toMillis(5));
  }

  @Override
  public @NonNull ClientZkReceiptOperations provideClientZkReceiptOperations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return provideClientZkOperations(signalServiceConfiguration).getReceiptOperations();
  }

  @Override
  public @NonNull KeyBackupService provideKeyBackupService(@NonNull SignalServiceAccountManager signalServiceAccountManager, @NonNull KeyStore keyStore, @NonNull KbsEnclave enclave) {
    return signalServiceAccountManager.getKeyBackupService(keyStore,
                                                           enclave.getEnclaveName(),
                                                           Hex.fromStringOrThrow(enclave.getServiceId()),
                                                           enclave.getMrEnclave(),
                                                           10);
  }

  @NonNull WebSocketFactory provideWebSocketFactory(@NonNull Supplier<SignalServiceConfiguration> signalServiceConfigurationSupplier, @NonNull SignalWebSocketHealthMonitor healthMonitor) {
    return new WebSocketFactory() {
      @Override
      public WebSocketConnection createWebSocket() {
        return new WebSocketConnection("normal",
                                       signalServiceConfigurationSupplier.get(),
                                       Optional.of(new DynamicCredentialsProvider()),
                                       BuildConfig.SIGNAL_AGENT,
                                       healthMonitor,
                                       Stories.isFeatureEnabled());
      }

      @Override
      public WebSocketConnection createUnidentifiedWebSocket() {
        return new WebSocketConnection("unidentified",
                                       signalServiceConfigurationSupplier.get(),
                                       Optional.empty(),
                                       BuildConfig.SIGNAL_AGENT,
                                       healthMonitor,
                                       Stories.isFeatureEnabled());
      }
    };
  }

  @VisibleForTesting
  static class DynamicCredentialsProvider implements CredentialsProvider {

    @Override
    public ACI getAci() {
      return SignalStore.account().getAci();
    }

    @Override
    public PNI getPni() {
      return SignalStore.account().getPni();
    }

    @Override
    public String getE164() {
      return SignalStore.account().getE164();
    }

    @Override
    public String getPassword() {
      return SignalStore.account().getServicePassword();
    }

    @Override
    public int getDeviceId() {
      return SignalStore.account().getDeviceId();
    }
  }
}
