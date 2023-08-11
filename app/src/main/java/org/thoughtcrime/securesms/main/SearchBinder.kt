package org.mycrimes.insecuretests.main

import android.widget.ImageView
import org.mycrimes.insecuretests.components.Material3SearchToolbar
import org.mycrimes.insecuretests.util.views.Stub

interface SearchBinder {
  fun getSearchAction(): ImageView

  fun getSearchToolbar(): Stub<Material3SearchToolbar>

  fun onSearchOpened()

  fun onSearchClosed()
}
