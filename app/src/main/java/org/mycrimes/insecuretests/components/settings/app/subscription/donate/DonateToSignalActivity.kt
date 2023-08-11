package org.mycrimes.insecuretests.components.settings.app.subscription.donate

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import org.mycrimes.insecuretests.R
import org.mycrimes.insecuretests.components.FragmentWrapperActivity
import org.mycrimes.insecuretests.components.settings.app.subscription.DonationPaymentComponent
import org.mycrimes.insecuretests.components.settings.app.subscription.StripeRepository

/**
 * Activity wrapper for donate to signal screen. An activity is needed because Google Pay uses the
 * activity [DonateToSignalActivity.startActivityForResult] flow that would be missed by a parent fragment.
 */
class DonateToSignalActivity : FragmentWrapperActivity(), DonationPaymentComponent {

  override val stripeRepository: StripeRepository by lazy { StripeRepository(this) }
  override val googlePayResultPublisher: Subject<DonationPaymentComponent.GooglePayResult> = PublishSubject.create()

  override fun getFragment(): Fragment {
    return NavHostFragment.create(R.navigation.donate_to_signal, DonateToSignalFragmentArgs.Builder(DonateToSignalType.ONE_TIME).build().toBundle())
  }

  @Suppress("DEPRECATION")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    googlePayResultPublisher.onNext(DonationPaymentComponent.GooglePayResult(requestCode, resultCode, data))
  }
}
