package org.mycrimes.insecuretests.service.webrtc

import com.google.protobuf.ByteString
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.ringrtc.RemotePeer
import org.whispersystems.signalservice.internal.push.SignalServiceProtos.SyncMessage.CallEvent

/**
 * Helper for creating call event sync messages.
 */
object CallEventSyncMessageUtil {
  @JvmStatic
  fun createAcceptedSyncMessage(remotePeer: RemotePeer, timestamp: Long, isOutgoing: Boolean, isVideoCall: Boolean): CallEvent {
    return createCallEvent(
      remotePeer.id,
      remotePeer.callId.longValue(),
      timestamp,
      isOutgoing,
      isVideoCall,
      CallEvent.Event.ACCEPTED
    )
  }

  @JvmStatic
  fun createNotAcceptedSyncMessage(remotePeer: RemotePeer, timestamp: Long, isOutgoing: Boolean, isVideoCall: Boolean): CallEvent {
    return createCallEvent(
      remotePeer.id,
      remotePeer.callId.longValue(),
      timestamp,
      isOutgoing,
      isVideoCall,
      CallEvent.Event.NOT_ACCEPTED
    )
  }

  @JvmStatic
  fun createDeleteCallEvent(remotePeer: RemotePeer, timestamp: Long, isOutgoing: Boolean, isVideoCall: Boolean): CallEvent {
    return createCallEvent(
      remotePeer.id,
      remotePeer.callId.longValue(),
      timestamp,
      isOutgoing,
      isVideoCall,
      CallEvent.Event.DELETE
    )
  }

  private fun createCallEvent(
    recipientId: RecipientId,
    callId: Long,
    timestamp: Long,
    isOutgoing: Boolean,
    isVideoCall: Boolean,
    event: CallEvent.Event
  ): CallEvent {
    val recipient = Recipient.resolved(recipientId)
    val callType = when {
      recipient.isCallLink -> CallEvent.Type.AD_HOC_CALL
      recipient.isGroup -> CallEvent.Type.GROUP_CALL
      isVideoCall -> CallEvent.Type.VIDEO_CALL
      else -> CallEvent.Type.AUDIO_CALL
    }

    val conversationId: ByteString = when {
      recipient.isCallLink -> recipient.requireCallLinkRoomId().encodeForProto()
      recipient.isGroup -> ByteString.copyFrom(recipient.requireGroupId().decodedId)
      else -> recipient.requireServiceId().toByteString()
    }

    return CallEvent
      .newBuilder()
      .setConversationId(conversationId)
      .setId(callId)
      .setTimestamp(timestamp)
      .setType(callType)
      .setDirection(if (isOutgoing) CallEvent.Direction.OUTGOING else CallEvent.Direction.INCOMING)
      .setEvent(event)
      .build()
  }
}