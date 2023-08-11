package org.mycrimes.insecuretests.dependencies;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.DeadlockDetector;
import org.signal.libsignal.zkgroup.profiles.ClientZkProfileOperations;
import org.signal.libsignal.zkgroup.receipts.ClientZkReceiptOperations;
import org.mycrimes.insecuretests.KbsEnclave;
import org.mycrimes.insecuretests.components.TypingStatusRepository;
import org.mycrimes.insecuretests.components.TypingStatusSender;
import org.mycrimes.insecuretests.crypto.storage.SignalServiceDataStoreImpl;
import org.mycrimes.insecuretests.database.DatabaseObserver;
import org.mycrimes.insecuretests.database.PendingRetryReceiptCache;
import org.mycrimes.insecuretests.jobmanager.JobManager;
import org.mycrimes.insecuretests.megaphone.MegaphoneRepository;
import org.mycrimes.insecuretests.messages.BackgroundMessageRetriever;
import org.mycrimes.insecuretests.messages.IncomingMessageObserver;
import org.mycrimes.insecuretests.notifications.MessageNotifier;
import org.mycrimes.insecuretests.payments.Payments;
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
import org.mycrimes.insecuretests.util.AppForegroundObserver;
import org.mycrimes.insecuretests.util.EarlyMessageCache;
import org.mycrimes.insecuretests.util.FrameRateTracker;
import org.mycrimes.insecuretests.video.exo.GiphyMp4Cache;
import org.mycrimes.insecuretests.video.exo.SimpleExoPlayerPool;
import org.mycrimes.insecuretests.webrtc.audio.AudioManagerCompat;
import org.whispersystems.signalservice.api.KeyBackupService;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalWebSocket;
import org.whispersystems.signalservice.api.groupsv2.GroupsV2Operations;
import org.whispersystems.signalservice.api.services.CallLinksService;
import org.whispersystems.signalservice.api.services.DonationsService;
import org.whispersystems.signalservice.api.services.ProfileService;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;

import java.security.KeyStore;
import java.util.function.Supplier;

import static org.mockito.Mockito.mock;

@SuppressWarnings("ConstantConditions")
public class MockApplicationDependencyProvider implements ApplicationDependencies.Provider {
  @Override
  public @NonNull GroupsV2Operations provideGroupsV2Operations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return null;
  }

  @Override
  public @NonNull SignalServiceAccountManager provideSignalServiceAccountManager(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return null;
  }

  @Override
  public @NonNull SignalServiceMessageSender provideSignalServiceMessageSender(@NonNull SignalWebSocket signalWebSocket, @NonNull SignalServiceDataStore protocolStore, @NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return null;
  }

  @Override
  public @NonNull SignalServiceMessageReceiver provideSignalServiceMessageReceiver(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return null;
  }

  @Override
  public @NonNull SignalServiceNetworkAccess provideSignalServiceNetworkAccess() {
    return null;
  }

  @Override
  public @NonNull BackgroundMessageRetriever provideBackgroundMessageRetriever() {
    return null;
  }

  @Override
  public @NonNull LiveRecipientCache provideRecipientCache() {
    return null;
  }

  @Override
  public @NonNull JobManager provideJobManager() {
    return mock(JobManager.class);
  }

  @Override
  public @NonNull FrameRateTracker provideFrameRateTracker() {
    return null;
  }

  @Override
  public @NonNull MegaphoneRepository provideMegaphoneRepository() {
    return null;
  }

  @Override
  public @NonNull EarlyMessageCache provideEarlyMessageCache() {
    return null;
  }

  @Override
  public @NonNull MessageNotifier provideMessageNotifier() {
    return null;
  }

  @Override
  public @NonNull IncomingMessageObserver provideIncomingMessageObserver() {
    return null;
  }

  @Override
  public @NonNull TrimThreadsByDateManager provideTrimThreadsByDateManager() {
    return null;
  }

  @Override
  public @NonNull ViewOnceMessageManager provideViewOnceMessageManager() {
    return null;
  }

  @Override
  public @NonNull ExpiringStoriesManager provideExpiringStoriesManager() {
    return null;
  }

  @Override
  public @NonNull ExpiringMessageManager provideExpiringMessageManager() {
    return null;
  }

  @Override
  public @NonNull DeletedCallEventManager provideDeletedCallEventManager() {
    return null;
  }

  @Override
  public @NonNull TypingStatusRepository provideTypingStatusRepository() {
    return null;
  }

  @Override
  public @NonNull TypingStatusSender provideTypingStatusSender() {
    return null;
  }

  @Override
  public @NonNull DatabaseObserver provideDatabaseObserver() {
    return mock(DatabaseObserver.class);
  }

  @Override
  public @NonNull Payments providePayments(@NonNull SignalServiceAccountManager signalServiceAccountManager) {
    return null;
  }

  @Override
  public @NonNull ShakeToReport provideShakeToReport() {
    return null;
  }

  @Override
  public @NonNull AppForegroundObserver provideAppForegroundObserver() {
    return mock(AppForegroundObserver.class);
  }

  @Override
  public @NonNull SignalCallManager provideSignalCallManager() {
    return null;
  }

  @Override
  public @NonNull PendingRetryReceiptManager providePendingRetryReceiptManager() {
    return null;
  }

  @Override
  public @NonNull PendingRetryReceiptCache providePendingRetryReceiptCache() {
    return null;
  }

  @Override
  public @NonNull SignalWebSocket provideSignalWebSocket(@NonNull Supplier<SignalServiceConfiguration> signalServiceConfigurationSupplier) {
    return null;
  }

  @Override
  public @NonNull SignalServiceDataStoreImpl provideProtocolStore() {
    return null;
  }

  @Override
  public @NonNull GiphyMp4Cache provideGiphyMp4Cache() {
    return null;
  }

  @Override
  public @NonNull SimpleExoPlayerPool provideExoPlayerPool() {
    return null;
  }

  @Override
  public @NonNull AudioManagerCompat provideAndroidCallAudioManager() {
    return null;
  }

  @Override
  public @NonNull DonationsService provideDonationsService(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return null;
  }

  @NonNull @Override public CallLinksService provideCallLinksService(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return null;
  }

  @Override
  public @NonNull ProfileService provideProfileService(@NonNull ClientZkProfileOperations profileOperations, @NonNull SignalServiceMessageReceiver signalServiceMessageReceiver, @NonNull SignalWebSocket signalWebSocket) {
    return null;
  }

  @Override
  public @NonNull DeadlockDetector provideDeadlockDetector() {
    return null;
  }

  @Override
  public @NonNull ClientZkReceiptOperations provideClientZkReceiptOperations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return null;
  }

  @Override
  public @NonNull KeyBackupService provideKeyBackupService(@NonNull SignalServiceAccountManager signalServiceAccountManager, @NonNull KeyStore keyStore, @NonNull KbsEnclave enclave) {
    return null;
  }

  @Override
  public @NonNull ScheduledMessageManager provideScheduledMessageManager() {
    return null;
  }
}
