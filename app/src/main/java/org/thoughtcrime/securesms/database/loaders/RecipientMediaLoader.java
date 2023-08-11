package org.mycrimes.insecuretests.database.loaders;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mycrimes.insecuretests.database.MediaTable;
import org.mycrimes.insecuretests.database.SignalDatabase;
import org.mycrimes.insecuretests.recipients.Recipient;
import org.mycrimes.insecuretests.recipients.RecipientId;

/**
 * It is more efficient to use the {@link ThreadMediaLoader} if you know the thread id already.
 */
public final class RecipientMediaLoader extends MediaLoader {

  @Nullable private final RecipientId           recipientId;
  @NonNull  private final MediaType          mediaType;
  @NonNull  private final MediaTable.Sorting sorting;

  public RecipientMediaLoader(@NonNull Context context,
                              @Nullable RecipientId recipientId,
                              @NonNull MediaType mediaType,
                              @NonNull MediaTable.Sorting sorting)
  {
    super(context);
    this.recipientId = recipientId;
    this.mediaType   = mediaType;
    this.sorting     = sorting;
  }

  @Override
  public Cursor getCursor() {
    if (recipientId == null || recipientId.isUnknown()) return null;

    long threadId = SignalDatabase.threads().getOrCreateThreadIdFor(Recipient.resolved(recipientId));

    return ThreadMediaLoader.createThreadMediaCursor(context, threadId, mediaType, sorting);
  }

}
