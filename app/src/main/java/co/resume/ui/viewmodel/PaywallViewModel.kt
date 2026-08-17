package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.resume.billing.BillingManager
import co.resume.billing.SubscriptionPlan
import co.resume.billing.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PaywallViewModel @Inject constructor(
    val billingManager: BillingManager,
    subscriptionRepository: SubscriptionRepository
) : ViewModel() {
    val plans: StateFlow<List<SubscriptionPlan>> = billingManager.plans
    val isPremium: StateFlow<Boolean> = subscriptionRepository.isPremium

    fun restorePurchases() = billingManager.queryActivePurchases()
}
