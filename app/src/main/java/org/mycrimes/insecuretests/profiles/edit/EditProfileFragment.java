package org.mycrimes.insecuretests.profiles.edit;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.airbnb.lottie.SimpleColorFilter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.signal.core.util.EditTextUtil;
import org.signal.core.util.StreamUtil;
import org.signal.core.util.concurrent.SimpleTask;
import org.signal.core.util.logging.Log;
import org.mycrimes.insecuretests.LoggingFragment;
import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.avatar.Avatars;
import org.mycrimes.insecuretests.avatar.picker.AvatarPickerFragment;
import org.mycrimes.insecuretests.databinding.ProfileCreateFragmentBinding;
import org.mycrimes.insecuretests.groups.GroupId;
import org.mycrimes.insecuretests.groups.ParcelableGroupId;
import org.mycrimes.insecuretests.keyvalue.PhoneNumberPrivacyValues;
import org.mycrimes.insecuretests.keyvalue.SignalStore;
import org.mycrimes.insecuretests.mediasend.Media;
import org.mycrimes.insecuretests.mms.GlideApp;
import org.mycrimes.insecuretests.profiles.edit.pnp.WhoCanSeeMyPhoneNumberFragment;
import org.mycrimes.insecuretests.profiles.manage.EditProfileNameFragment;
import org.mycrimes.insecuretests.providers.BlobProvider;
import org.mycrimes.insecuretests.util.CommunicationActions;
import org.mycrimes.insecuretests.util.FeatureFlags;
import org.mycrimes.insecuretests.util.ViewUtil;
import org.mycrimes.insecuretests.util.navigation.SafeNavigation;
import org.mycrimes.insecuretests.util.text.AfterTextChanged;

import java.io.IOException;
import java.io.InputStream;

import static org.mycrimes.insecuretests.profiles.edit.EditProfileActivity.EXCLUDE_SYSTEM;
import static org.mycrimes.insecuretests.profiles.edit.EditProfileActivity.GROUP_ID;
import static org.mycrimes.insecuretests.profiles.edit.EditProfileActivity.NEXT_BUTTON_TEXT;
import static org.mycrimes.insecuretests.profiles.edit.EditProfileActivity.NEXT_INTENT;
import static org.mycrimes.insecuretests.profiles.edit.EditProfileActivity.SHOW_TOOLBAR;

/**
 * Used for profile creation during registration.
 */
public class EditProfileFragment extends LoggingFragment {

  private static final String TAG                    = Log.tag(EditProfileFragment.class);
  private static final int    MAX_DESCRIPTION_GLYPHS = 480;
  private static final int    MAX_DESCRIPTION_BYTES  = 8192;

  private Intent nextIntent;

  private EditProfileViewModel         viewModel;
  private ProfileCreateFragmentBinding binding;

  private Controller controller;

  @Override
  public void onAttach(@NonNull Context context) {
    super.onAttach(context);

    if (context instanceof Controller) {
      controller = (Controller) context;
    } else {
      throw new IllegalStateException("Context must subclass Controller");
    }
  }

  @Override
  public @Nullable View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = ProfileCreateFragmentBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    GroupId groupId = GroupId.parseNullableOrThrow(requireArguments().getString(GROUP_ID, null));

    initializeViewModel(requireArguments().getBoolean(EXCLUDE_SYSTEM, false), groupId, savedInstanceState != null);
    initializeResources(groupId);
    initializeProfileAvatar();
    initializeProfileName();

    getParentFragmentManager().setFragmentResultListener(AvatarPickerFragment.REQUEST_KEY_SELECT_AVATAR, getViewLifecycleOwner(), (key, bundle) -> {
      if (bundle.getBoolean(AvatarPickerFragment.SELECT_AVATAR_CLEAR)) {
        viewModel.setAvatarMedia(null);
        viewModel.setAvatar(null);
        binding.avatar.setImageDrawable(null);
      } else {
        Media media = bundle.getParcelable(AvatarPickerFragment.SELECT_AVATAR_MEDIA);
        handleMediaFromResult(media);
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  private void handleMediaFromResult(@NonNull Media media) {
    SimpleTask.run(() -> {
      try {
        InputStream stream = BlobProvider.getInstance().getStream(requireContext(), media.getUri());

        return StreamUtil.readFully(stream);
      } catch (IOException ioException) {
        Log.w(TAG, ioException);
        return null;
      }
    },
    (avatarBytes) -> {
      if (avatarBytes != null) {
        viewModel.setAvatarMedia(media);
        viewModel.setAvatar(avatarBytes);
        GlideApp.with(EditProfileFragment.this)
                .load(avatarBytes)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .circleCrop()
                .into(binding.avatar);
      } else {
        Toast.makeText(requireActivity(), R.string.CreateProfileActivity_error_setting_profile_photo, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void initializeViewModel(boolean excludeSystem, @Nullable GroupId groupId, boolean hasSavedInstanceState) {
    EditProfileRepository repository;

    if (groupId != null) {
      repository = new EditGroupProfileRepository(requireContext(), groupId);
    } else {
      repository = new EditSelfProfileRepository(requireContext(), excludeSystem);
    }

    EditProfileViewModel.Factory factory = new EditProfileViewModel.Factory(repository, hasSavedInstanceState, groupId);

    viewModel = new ViewModelProvider(requireActivity(), factory).get(EditProfileViewModel.class);
  }

  private void initializeResources(@Nullable GroupId groupId) {
    Bundle  arguments      = requireArguments();
    boolean isEditingGroup = groupId != null;

    this.nextIntent = arguments.getParcelable(NEXT_INTENT);

    binding.avatar.setOnClickListener(v -> startAvatarSelection());
    binding.mmsGroupHint.setVisibility(isEditingGroup && groupId.isMms() ? View.VISIBLE : View.GONE);

    if (isEditingGroup) {
      EditTextUtil.addGraphemeClusterLimitFilter(binding.givenName, FeatureFlags.getMaxGroupNameGraphemeLength());
      binding.profileDescriptionText.setVisibility(View.GONE);
      binding.whoCanFindMeContainer.setVisibility(View.GONE);
      binding.givenName.addTextChangedListener(new AfterTextChanged(s -> viewModel.setGivenName(s.toString())));
      binding.givenNameWrapper.setHint(R.string.EditProfileFragment__group_name);
      binding.givenName.requestFocus();
      binding.toolbar.setTitle(R.string.EditProfileFragment__edit_group);
      binding.namePreview.setVisibility(View.GONE);

      if (groupId.isV2()) {
        EditTextUtil.addGraphemeClusterLimitFilter(binding.familyName, MAX_DESCRIPTION_GLYPHS);
        binding.familyName.addTextChangedListener(new AfterTextChanged(s -> {
          EditProfileNameFragment.trimFieldToMaxByteLength(s, MAX_DESCRIPTION_BYTES);
          viewModel.setFamilyName(s.toString());
        }));
        binding.familyNameWrapper.setHint(R.string.EditProfileFragment__group_description);
        binding.familyName.setSingleLine(false);
        binding.familyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        binding.groupDescriptionText.setLearnMoreVisible(false);
        binding.groupDescriptionText.setText(R.string.CreateProfileActivity_group_descriptions_will_be_visible_to_members_of_this_group_and_people_who_have_been_invited);
      } else {
        binding.familyNameWrapper.setVisibility(View.GONE);
        binding.familyName.setEnabled(false);
        binding.groupDescriptionText.setVisibility(View.GONE);
      }
      binding.avatarPlaceholder.setImageResource(R.drawable.ic_group_outline_40);
    } else {
      EditTextUtil.addGraphemeClusterLimitFilter(binding.givenName, EditProfileNameFragment.NAME_MAX_GLYPHS);
      EditTextUtil.addGraphemeClusterLimitFilter(binding.familyName, EditProfileNameFragment.NAME_MAX_GLYPHS);
      binding.givenName.addTextChangedListener(new AfterTextChanged(s -> {
                                                                        EditProfileNameFragment.trimFieldToMaxByteLength(s);
                                                                        viewModel.setGivenName(s.toString());
                                                                      }));
      binding.familyName.addTextChangedListener(new AfterTextChanged(s -> {
                                                                         EditProfileNameFragment.trimFieldToMaxByteLength(s);
                                                                         viewModel.setFamilyName(s.toString());
                                                                       }));
      binding.groupDescriptionText.setVisibility(View.GONE);
      binding.profileDescriptionText.setLearnMoreVisible(true);
      binding.profileDescriptionText.setLinkColor(ContextCompat.getColor(requireContext(), R.color.signal_colorPrimary));
      binding.profileDescriptionText.setOnLinkClickListener(v -> CommunicationActions.openBrowserLink(requireContext(), getString(R.string.EditProfileFragment__support_link)));

      if (FeatureFlags.phoneNumberPrivacy()) {
        getParentFragmentManager().setFragmentResultListener(WhoCanSeeMyPhoneNumberFragment.REQUEST_KEY, getViewLifecycleOwner(), (requestKey, result) -> {
          if (WhoCanSeeMyPhoneNumberFragment.REQUEST_KEY.equals(requestKey)) {
            presentWhoCanFindMeDescription(SignalStore.phoneNumberPrivacy().getPhoneNumberListingMode());
          }
        });

        binding.whoCanFindMeContainer.setVisibility(View.VISIBLE);
        binding.whoCanFindMeContainer.setOnClickListener(v -> SafeNavigation.safeNavigate(Navigation.findNavController(v), EditProfileFragmentDirections.actionCreateProfileFragmentToPhoneNumberPrivacy()));
        presentWhoCanFindMeDescription(SignalStore.phoneNumberPrivacy().getPhoneNumberListingMode());
      }
    }

    binding.finishButton.setOnClickListener(v -> {
      binding.finishButton.setSpinning();
      handleUpload();
    });

    binding.finishButton.setText(arguments.getInt(NEXT_BUTTON_TEXT, R.string.CreateProfileActivity_next));

    if (arguments.getBoolean(SHOW_TOOLBAR, true)) {
      binding.toolbar.setVisibility(View.VISIBLE);
      binding.toolbar.setNavigationOnClickListener(v -> requireActivity().finish());
      binding.title.setVisibility(View.GONE);
    }
  }

  private void initializeProfileName() {
    viewModel.isFormValid().observe(getViewLifecycleOwner(), isValid -> {
      binding.finishButton.setEnabled(isValid);
      binding.finishButton.setAlpha(isValid ? 1f : 0.5f);
    });

    viewModel.givenName().observe(getViewLifecycleOwner(), givenName -> updateFieldIfNeeded(binding.givenName, givenName));

    viewModel.familyName().observe(getViewLifecycleOwner(), familyName -> updateFieldIfNeeded(binding.familyName, familyName));

    viewModel.profileName().observe(getViewLifecycleOwner(), profileName -> binding.namePreview.setText(profileName.toString()));
  }

  private void initializeProfileAvatar() {
    viewModel.avatar().observe(getViewLifecycleOwner(), bytes -> {
      if (bytes == null) {
        GlideApp.with(this).clear(binding.avatar);
        return;
      }

      GlideApp.with(this)
              .load(bytes)
              .circleCrop()
              .into(binding.avatar);
    });

    viewModel.avatarColor().observe(getViewLifecycleOwner(), avatarColor -> {
      Avatars.ForegroundColor foregroundColor = Avatars.getForegroundColor(avatarColor);

      binding.avatarPlaceholder.getDrawable().setColorFilter(new SimpleColorFilter(foregroundColor.getColorInt()));
      binding.avatarBackground.getDrawable().setColorFilter(new SimpleColorFilter(avatarColor.colorInt()));
    });
  }

  private static void updateFieldIfNeeded(@NonNull EditText field, @NonNull String value) {
    String fieldTrimmed = field.getText().toString().trim();
    String valueTrimmed = value.trim();

    if (!fieldTrimmed.equals(valueTrimmed)) {
      boolean setSelectionToEnd = field.getText().length() == 0;

      field.setText(value);

      if (setSelectionToEnd) {
        field.setSelection(field.getText().length());
      }
    }
  }

  private void presentWhoCanFindMeDescription(PhoneNumberPrivacyValues.PhoneNumberListingMode phoneNumberListingMode) {
    switch (phoneNumberListingMode) {
      case LISTED:
        binding.whoCanFindMeDescription.setText(R.string.PhoneNumberPrivacy_everyone);
        break;
      case UNLISTED:
        binding.whoCanFindMeDescription.setText(R.string.PhoneNumberPrivacy_nobody);
        break;
    }
  }

  private void startAvatarSelection() {
    if (viewModel.isGroup()) {
      Parcelable groupId = ParcelableGroupId.from(viewModel.getGroupId());
      SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), EditProfileFragmentDirections.actionCreateProfileFragmentToAvatarPicker((ParcelableGroupId) groupId, viewModel.getAvatarMedia()));
    } else {
      SafeNavigation.safeNavigate(Navigation.findNavController(requireView()), EditProfileFragmentDirections.actionCreateProfileFragmentToAvatarPicker(null, null));
    }
  }

  private void handleUpload() {
    viewModel.getUploadResult().observe(getViewLifecycleOwner(), uploadResult -> {
      if (uploadResult == EditProfileRepository.UploadResult.SUCCESS) {
        if (!viewModel.isGroup()) {
          handleFinishedLollipop();
        }
        else {
          handleFinishedLegacy();
        }
      } else {
        Toast.makeText(requireContext(), R.string.CreateProfileActivity_problem_setting_profile, Toast.LENGTH_LONG).show();
      }
    });

    viewModel.submitProfile();
  }

  private void handleFinishedLegacy() {
    ViewUtil.hideKeyboard(requireContext(), binding.finishButton);
    binding.finishButton.cancelSpinning();
    if (nextIntent != null) startActivity(nextIntent);

    controller.onProfileNameUploadCompleted();
  }

  private void handleFinishedLollipop() {
    int[] finishButtonLocation = new int[2];
    int[] revealLocation       = new int[2];

    binding.finishButton.getLocationInWindow(finishButtonLocation);
    binding.reveal.getLocationInWindow(revealLocation);

    int finishX = finishButtonLocation[0] - revealLocation[0];
    int finishY = finishButtonLocation[1] - revealLocation[1];

    finishX += binding.finishButton.getWidth() / 2;
    finishY += binding.finishButton.getHeight() / 2;

    Animator animation = ViewAnimationUtils.createCircularReveal(binding.reveal, finishX, finishY, 0f, (float) Math.max(binding.reveal.getWidth(), binding.reveal.getHeight()));
    animation.setDuration(500);
    animation.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {}

      @Override
      public void onAnimationEnd(Animator animation) {
        ViewUtil.hideKeyboard(requireContext(), binding.finishButton);
        binding.finishButton.cancelSpinning();
        if (nextIntent != null && getActivity() != null) {
          startActivity(nextIntent);
        }

        controller.onProfileNameUploadCompleted();
      }

      @Override
      public void onAnimationCancel(Animator animation) {}

      @Override
      public void onAnimationRepeat(Animator animation) {}
    });

    binding.reveal.setVisibility(View.VISIBLE);
    animation.start();
  }

  public interface Controller {
    void onProfileNameUploadCompleted();
  }
}