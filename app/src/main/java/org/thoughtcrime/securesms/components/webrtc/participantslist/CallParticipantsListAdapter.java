package org.mycrimes.insecuretests.components.webrtc.participantslist;

import org.mycrimes.insecuretests.R;
import org.mycrimes.insecuretests.util.adapter.mapping.LayoutFactory;
import org.mycrimes.insecuretests.util.adapter.mapping.MappingAdapter;

public class CallParticipantsListAdapter extends MappingAdapter {

  CallParticipantsListAdapter() {
    registerFactory(CallParticipantsListHeader.class, new LayoutFactory<>(CallParticipantsListHeaderViewHolder::new, R.layout.call_participants_list_header));
    registerFactory(CallParticipantViewState.class, new LayoutFactory<>(CallParticipantViewHolder::new, R.layout.call_participants_list_item));
  }

}
