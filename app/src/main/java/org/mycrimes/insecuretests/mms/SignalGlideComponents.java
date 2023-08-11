package org.mycrimes.insecuretests.mms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.UnitModelLoader;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;

import org.signal.glide.apng.decode.APNGDecoder;
import org.mycrimes.insecuretests.badges.models.Badge;
import org.mycrimes.insecuretests.blurhash.BlurHash;
import org.mycrimes.insecuretests.blurhash.BlurHashModelLoader;
import org.mycrimes.insecuretests.blurhash.BlurHashResourceDecoder;
import org.mycrimes.insecuretests.contacts.avatars.ContactPhoto;
import org.mycrimes.insecuretests.crypto.AttachmentSecret;
import org.mycrimes.insecuretests.crypto.AttachmentSecretProvider;
import org.mycrimes.insecuretests.giph.model.ChunkedImageUrl;
import org.mycrimes.insecuretests.glide.BadgeLoader;
import org.mycrimes.insecuretests.glide.ChunkedImageUrlLoader;
import org.mycrimes.insecuretests.glide.ContactPhotoLoader;
import org.mycrimes.insecuretests.glide.GiftBadgeModel;
import org.mycrimes.insecuretests.glide.OkHttpUrlLoader;
import org.mycrimes.insecuretests.glide.cache.ApngBufferCacheDecoder;
import org.mycrimes.insecuretests.glide.cache.ApngFrameDrawableTranscoder;
import org.mycrimes.insecuretests.glide.cache.ApngStreamCacheDecoder;
import org.mycrimes.insecuretests.glide.cache.EncryptedApngCacheEncoder;
import org.mycrimes.insecuretests.glide.cache.EncryptedBitmapResourceEncoder;
import org.mycrimes.insecuretests.glide.cache.EncryptedCacheDecoder;
import org.mycrimes.insecuretests.glide.cache.EncryptedCacheEncoder;
import org.mycrimes.insecuretests.glide.cache.EncryptedGifDrawableResourceEncoder;
import org.mycrimes.insecuretests.mms.AttachmentStreamUriLoader.AttachmentModel;
import org.mycrimes.insecuretests.mms.DecryptableStreamUriLoader.DecryptableUri;
import org.mycrimes.insecuretests.stickers.StickerRemoteUri;
import org.mycrimes.insecuretests.stickers.StickerRemoteUriLoader;
import org.mycrimes.insecuretests.stories.StoryTextPostModel;
import org.mycrimes.insecuretests.util.ConversationShortcutPhoto;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * The core logic for {@link SignalGlideModule}. This is a separate class because it uses
 * dependencies defined in the main Gradle module.
 */
public class SignalGlideComponents implements RegisterGlideComponents {

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
    AttachmentSecret attachmentSecret = AttachmentSecretProvider.getInstance(context).getOrCreateAttachmentSecret();
    byte[]           secret           = attachmentSecret.getModernKey();

    registry.prepend(File.class, File.class, UnitModelLoader.Factory.getInstance());

    registry.prepend(InputStream.class, new EncryptedCacheEncoder(secret, glide.getArrayPool()));

    registry.prepend(Bitmap.class, new EncryptedBitmapResourceEncoder(secret));
    registry.prepend(File.class, Bitmap.class, new EncryptedCacheDecoder<>(secret, new StreamBitmapDecoder(new Downsampler(registry.getImageHeaderParsers(), context.getResources().getDisplayMetrics(), glide.getBitmapPool(), glide.getArrayPool()), glide.getArrayPool())));

    registry.prepend(GifDrawable.class, new EncryptedGifDrawableResourceEncoder(secret));
    registry.prepend(File.class, GifDrawable.class, new EncryptedCacheDecoder<>(secret, new StreamGifDecoder(registry.getImageHeaderParsers(), new ByteBufferGifDecoder(context, registry.getImageHeaderParsers(), glide.getBitmapPool(), glide.getArrayPool()), glide.getArrayPool())));

    ApngBufferCacheDecoder apngBufferCacheDecoder = new ApngBufferCacheDecoder();
    ApngStreamCacheDecoder apngStreamCacheDecoder = new ApngStreamCacheDecoder(apngBufferCacheDecoder);

    registry.prepend(InputStream.class, APNGDecoder.class, apngStreamCacheDecoder);
    registry.prepend(ByteBuffer.class, APNGDecoder.class, apngBufferCacheDecoder);
    registry.prepend(APNGDecoder.class, new EncryptedApngCacheEncoder(secret));
    registry.prepend(File.class, APNGDecoder.class, new EncryptedCacheDecoder<>(secret, apngStreamCacheDecoder));
    registry.register(APNGDecoder.class, Drawable.class, new ApngFrameDrawableTranscoder());

    registry.prepend(BlurHash.class, Bitmap.class, new BlurHashResourceDecoder());
    registry.prepend(StoryTextPostModel.class, Bitmap.class, new StoryTextPostModel.Decoder());

    registry.append(StoryTextPostModel.class, StoryTextPostModel.class, UnitModelLoader.Factory.getInstance());
    registry.append(ConversationShortcutPhoto.class, Bitmap.class, new ConversationShortcutPhoto.Loader.Factory(context));
    registry.append(ContactPhoto.class, InputStream.class, new ContactPhotoLoader.Factory(context));
    registry.append(DecryptableUri.class, InputStream.class, new DecryptableStreamUriLoader.Factory(context));
    registry.append(AttachmentModel.class, InputStream.class, new AttachmentStreamUriLoader.Factory());
    registry.append(ChunkedImageUrl.class, InputStream.class, new ChunkedImageUrlLoader.Factory());
    registry.append(StickerRemoteUri.class, InputStream.class, new StickerRemoteUriLoader.Factory());
    registry.append(BlurHash.class, BlurHash.class, new BlurHashModelLoader.Factory());
    registry.append(Badge.class, InputStream.class, BadgeLoader.createFactory());
    registry.append(GiftBadgeModel.class, InputStream.class, GiftBadgeModel.createFactory());
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
  }
}
