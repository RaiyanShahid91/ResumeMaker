package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.resume.ads.AdManager
import co.resume.billing.SubscriptionDetails
import co.resume.billing.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AdViewModel @Inject constructor(
    val adManager: AdManager,
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val isPremium: StateFlow<Boolean> = subscriptionRepository.isPremium
    val subscriptionDetails: StateFlow<SubscriptionDetails?> = subscriptionRepository.subscriptionDetails
}
