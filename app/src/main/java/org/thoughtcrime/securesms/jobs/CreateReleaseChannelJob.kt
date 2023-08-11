package org.mycrimes.insecuretests.jobs

import androidx.core.content.ContextCompat
import org.signal.core.util.logging.Log
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.avatar.Avatar
import org.mycrimes.insecuretests.avatar.AvatarRenderer
import org.mycrimes.insecuretests.avatar.Avatars
import org.mycrimes.insecuretests.database.SignalDatabase
import org.mycrimes.insecuretests.jobmanager.Job
import org.mycrimes.insecuretests.keyvalue.SignalStore
import org.mycrimes.insecuretests.profiles.AvatarHelper
import org.mycrimes.insecuretests.profiles.ProfileName
import org.mycrimes.insecuretests.providers.BlobProvider
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.transport.RetryLaterException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Creates the Release Channel (Signal) recipient.
 */
class CreateReleaseChannelJob private constructor(parameters: Parameters) : BaseJob(parameters) {
  companion object {
    const val KEY = "CreateReleaseChannelJob"

    private val TAG = Log.tag(CreateReleaseChannelJob::class.java)

    fun create(): CreateReleaseChannelJob {
      return CreateReleaseChannelJob(
        Parameters.Builder()
          .setQueue("CreateReleaseChannelJob")
          .setMaxInstancesForFactory(1)
          .setMaxAttempts(3)
          .build()
      )
    }
  }

  override fun serialize(): ByteArray? = null

  override fun getFactoryKey(): String = KEY

  override fun onFailure() = Unit

  override fun onRun() {
    if (!SignalStore.account().isRegistered) {
      Log.i(TAG, "Not registered, skipping.")
      return
    }

    if (SignalStore.releaseChannelValues().releaseChannelRecipientId != null) {
      Log.i(TAG, "Already created Release Channel recipient ${SignalStore.releaseChannelValues().releaseChannelRecipientId}")

      val recipient = Recipient.resolved(SignalStore.releaseChannelValues().releaseChannelRecipientId!!)
      if (recipient.profileAvatar == null || recipient.profileAvatar?.isEmpty() == true) {
        setAvatar(recipient.id)
      }
    } else {
      val recipients = SignalDatabase.recipients

      val releaseChannelId: RecipientId = recipients.insertReleaseChannelRecipient()
      SignalStore.releaseChannelValues().setReleaseChannelRecipientId(releaseChannelId)

      recipients.setProfileName(releaseChannelId, ProfileName.asGiven("Signal"))
      recipients.setMuted(releaseChannelId, Long.MAX_VALUE)
      setAvatar(releaseChannelId)
    }
  }

  private fun setAvatar(id: RecipientId) {
    val latch = CountDownLatch(1)
    AvatarRenderer.renderAvatar(
      context,
      Avatar.Resource(
        R.drawable.ic_signal_logo_large,
        Avatars.ColorPair(ContextCompat.getColor(context, R.color.core_ultramarine), ContextCompat.getColor(context, R.color.core_white), "")
      ),
      onAvatarRendered = { media ->
        AvatarHelper.setAvatar(context, id, BlobProvider.getInstance().getStream(context, media.uri))
        SignalDatabase.recipients.setProfileAvatar(id, "local")
        latch.countDown()
      },
      onRenderFailed = { t ->
        Log.w(TAG, t)
        latch.countDown()
      }
    )

    try {
      val completed: Boolean = latch.await(30, TimeUnit.SECONDS)
      if (!completed) {
        throw RetryLaterException()
      }
    } catch (e: InterruptedException) {
      throw RetryLaterException()
    }
  }

  override fun onShouldRetry(e: Exception): Boolean = e is RetryLaterException

  class Factory : Job.Factory<CreateReleaseChannelJob> {
    override fun create(parameters: Parameters, serializedData: ByteArray?): CreateReleaseChannelJob {
      return CreateReleaseChannelJob(parameters)
    }
  }
}
