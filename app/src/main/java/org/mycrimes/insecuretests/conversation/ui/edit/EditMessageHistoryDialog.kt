package org.mycrimes.insecuretests.conversation.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.signal.core.util.concurrent.LifecycleDisposable
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.FixedRoundedCornerBottomSheetDialogFragment
import org.mycrimes.insecuretests.components.ViewBinderDelegate
import org.mycrimes.insecuretests.conversation.ConversationAdapter
import org.mycrimes.insecuretests.conversation.ConversationBottomSheetCallback
import org.mycrimes.insecuretests.conversation.ConversationItemDisplayMode
import org.mycrimes.insecuretests.conversation.ConversationMessage
import org.mycrimes.insecuretests.conversation.colors.Colorizer
import org.mycrimes.insecuretests.conversation.colors.RecyclerViewColorizer
import org.mycrimes.insecuretests.conversation.mutiselect.MultiselectPart
import org.mycrimes.insecuretests.conversation.quotes.OriginalMessageSeparatorDecoration
import org.mycrimes.insecuretests.database.model.MessageRecord
import org.mycrimes.insecuretests.database.model.MmsMessageRecord
import org.mycrimes.insecuretests.databinding.MessageEditHistoryBottomSheetBinding
import org.mycrimes.insecuretests.giph.mp4.GiphyMp4ItemDecoration
import org.mycrimes.insecuretests.giph.mp4.GiphyMp4PlaybackController
import org.mycrimes.insecuretests.giph.mp4.GiphyMp4PlaybackPolicy
import org.mycrimes.insecuretests.giph.mp4.GiphyMp4ProjectionPlayerHolder
import org.mycrimes.insecuretests.giph.mp4.GiphyMp4ProjectionRecycler
import org.mycrimes.insecuretests.groups.GroupId
import org.mycrimes.insecuretests.groups.GroupMigrationMembershipChange
import org.mycrimes.insecuretests.mms.GlideApp
import org.mycrimes.insecuretests.recipients.Recipient
import org.mycrimes.insecuretests.recipients.RecipientId
import org.mycrimes.insecuretests.util.BottomSheetUtil
import org.mycrimes.insecuretests.util.ViewModelFactory
import org.mycrimes.insecuretests.util.fragments.requireListener
import java.util.Locale

/**
 * Show history of edits for a specific message.
 */
class EditMessageHistoryDialog : FixedRoundedCornerBottomSheetDialogFragment() {

  override val peekHeightPercentage: Float = 0.4f

  private val binding: MessageEditHistoryBottomSheetBinding by ViewBinderDelegate(MessageEditHistoryBottomSheetBinding::bind)
  private val originalMessageId: Long by lazy { requireArguments().getLong(ARGUMENT_ORIGINAL_MESSAGE_ID) }
  private val conversationRecipient: Recipient by lazy { Recipient.resolved(requireArguments().getParcelable(ARGUMENT_CONVERSATION_RECIPIENT_ID)!!) }
  private val viewModel: EditMessageHistoryViewModel by viewModels(factoryProducer = ViewModelFactory.factoryProducer { EditMessageHistoryViewModel(originalMessageId, conversationRecipient) })

  private val disposables: LifecycleDisposable = LifecycleDisposable()

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = MessageEditHistoryBottomSheetBinding.inflate(inflater, container, false).root
    view.minimumHeight = (resources.displayMetrics.heightPixels * peekHeightPercentage).toInt()
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    disposables.bindTo(viewLifecycleOwner)

    val colorizer = Colorizer()

    val messageAdapter = ConversationAdapter(
      requireContext(),
      viewLifecycleOwner,
      GlideApp.with(this),
      Locale.getDefault(),
      ConversationAdapterListener(),
      conversationRecipient.hasWallpaper(),
      colorizer
    ).apply {
      setCondensedMode(ConversationItemDisplayMode.EXTRA_CONDENSED)
    }

    binding.editHistoryList.apply {
      layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
      adapter = messageAdapter
      itemAnimator = null
      addItemDecoration(OriginalMessageSeparatorDecoration(context, R.string.EditMessageHistoryDialog_title))
    }

    val recyclerViewColorizer = RecyclerViewColorizer(binding.editHistoryList)

    disposables += viewModel
      .getEditHistory()
      .subscribeBy { messages ->
        if (messages.isEmpty()) {
          dismiss()
        }

        messageAdapter.submitList(messages)
        recyclerViewColorizer.setChatColors(conversationRecipient.chatColors)
      }

    disposables += viewModel.getNameColorsMap().subscribe { map ->
      colorizer.onNameColorsChanged(map)
      messageAdapter.notifyItemRangeChanged(0, messageAdapter.itemCount, ConversationAdapter.PAYLOAD_NAME_COLORS)
    }

    initializeGiphyMp4()
  }

  private fun initializeGiphyMp4(): GiphyMp4ProjectionRecycler {
    val maxPlayback = GiphyMp4PlaybackPolicy.maxSimultaneousPlaybackInConversation()
    val holders = GiphyMp4ProjectionPlayerHolder.injectVideoViews(
      requireContext(),
      viewLifecycleOwner.lifecycle,
      binding.videoContainer,
      maxPlayback
    )
    val callback = GiphyMp4ProjectionRecycler(holders)

    GiphyMp4PlaybackController.attach(binding.editHistoryList, callback, maxPlayback)
    binding.editHistoryList.addItemDecoration(GiphyMp4ItemDecoration(callback) {}, 0)

    return callback
  }

  private inner class ConversationAdapterListener : ConversationAdapter.ItemClickListener by requireListener<ConversationBottomSheetCallback>().getConversationAdapterListener() {
    override fun onQuoteClicked(messageRecord: MmsMessageRecord) = Unit
    override fun onScheduledIndicatorClicked(view: View, conversationMessage: ConversationMessage) = Unit
    override fun onGroupMemberClicked(recipientId: RecipientId, groupId: GroupId) = Unit
    override fun onItemClick(item: MultiselectPart) = Unit
    override fun onItemLongClick(itemView: View, item: MultiselectPart) = Unit
    override fun onQuotedIndicatorClicked(messageRecord: MessageRecord) = Unit
    override fun onReactionClicked(multiselectPart: MultiselectPart, messageId: Long, isMms: Boolean) = Unit
    override fun onMessageWithRecaptchaNeededClicked(messageRecord: MessageRecord) = Unit
    override fun onGroupMigrationLearnMoreClicked(membershipChange: GroupMigrationMembershipChange) = Unit
    override fun onChatSessionRefreshLearnMoreClicked() = Unit
    override fun onBadDecryptLearnMoreClicked(author: RecipientId) = Unit
    override fun onSafetyNumberLearnMoreClicked(recipient: Recipient) = Unit
    override fun onJoinGroupCallClicked() = Unit
    override fun onInviteFriendsToGroupClicked(groupId: GroupId.V2) = Unit
    override fun onEnableCallNotificationsClicked() = Unit
    override fun onCallToAction(action: String) = Unit
    override fun onDonateClicked() = Unit
    override fun onRecipientNameClicked(target: RecipientId) = Unit
    override fun onViewGiftBadgeClicked(messageRecord: MessageRecord) = Unit
    override fun onActivatePaymentsClicked() = Unit
    override fun onSendPaymentClicked(recipientId: RecipientId) = Unit
    override fun onEditedIndicatorClicked(messageRecord: MessageRecord) = Unit
  }

  companion object {
    private const val ARGUMENT_ORIGINAL_MESSAGE_ID = "message_id"
    private const val ARGUMENT_CONVERSATION_RECIPIENT_ID = "recipient_id"

    @JvmStatic
    fun show(fragmentManager: FragmentManager, threadRecipient: RecipientId, messageRecord: MessageRecord) {
      EditMessageHistoryDialog()
        .apply {
          arguments = bundleOf(
            ARGUMENT_ORIGINAL_MESSAGE_ID to (messageRecord.originalMessageId?.id ?: messageRecord.id),
            ARGUMENT_CONVERSATION_RECIPIENT_ID to threadRecipient
          )
        }
        .show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }
}
