package co.resume.ui.screen.paywall

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resume.billing.BillingConstants
import co.resume.billing.SubscriptionPlan
import co.resume.ui.component.AppButton
import co.resume.ui.component.AppCard
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.PaywallViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val plans by viewModel.plans.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity

    // Once Play confirms the purchase, isPremium flips true and we simply leave the paywall —
    // there's nothing further for the user to do here.
    LaunchedEffect(isPremium) { if (isPremium) onBack() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.restorePurchases() }) {
                        Text("Restore")
                    }
                }
            )
        }
    ) { padding ->
        PaywallContent(
            plans = plans,
            modifier = Modifier.padding(padding),
            onSubscribe = { offerToken ->
                activity?.let { viewModel.billingManager.launchPurchase(it, offerToken) }
            }
        )
    }
}

@Composable
private fun PaywallContent(
    plans: List<SubscriptionPlan>,
    modifier: Modifier = Modifier,
    onSubscribe: (offerToken: String) -> Unit
) {
    var selectedBasePlan by remember { mutableStateOf(BillingConstants.YEARLY_BASE_PLAN_ID) }
    val selectedPlan = plans.find { it.basePlanId == selectedBasePlan }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
        }

        Text(
            "Go Premium",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            "Remove all ads and unlock the full app",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        AppCard(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                BenefitRow(Icons.Filled.Bolt, "No ads, ever")
                BenefitRow(Icons.Filled.Description, "Unlimited resumes & cover letters")
                BenefitRow(Icons.Filled.Check, "All premium templates unlocked")
            }
        }

        Spacer(Modifier.height(24.dp))

        if (plans.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.padding(vertical = 24.dp))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                plans.sortedByDescending { it.basePlanId == BillingConstants.YEARLY_BASE_PLAN_ID }
                    .forEach { plan ->
                        PlanCard(
                            plan = plan,
                            selected = plan.basePlanId == selectedBasePlan,
                            onClick = { selectedBasePlan = plan.basePlanId }
                        )
                    }
            }

            AppButton(
                text = "Continue",
                onClick = { selectedPlan?.let { onSubscribe(it.offerToken) } },
                enabled = selectedPlan != null,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp)
            )
        }

        Text(
            "Cancel anytime in Google Play → Subscriptions. Payment is charged to your Google account.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun BenefitRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 12.dp))
    }
}

@Composable
private fun PlanCard(plan: SubscriptionPlan, selected: Boolean, onClick: () -> Unit) {
    val isYearly = plan.basePlanId == BillingConstants.YEARLY_BASE_PLAN_ID
    AppCard(
        onClick = onClick,
        containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (isYearly) "Yearly" else "Monthly",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isYearly) {
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.tertiary)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "BEST VALUE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    "${plan.formattedPrice} / ${if (isYearly) "year" else "month"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .border(
                        width = 2.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PaywallContentPreview() {
    ResumeBuilderTheme {
        PaywallContent(
            plans = listOf(
                SubscriptionPlan(BillingConstants.MONTHLY_BASE_PLAN_ID, "monthly-token", "$9.99"),
                SubscriptionPlan(BillingConstants.YEARLY_BASE_PLAN_ID, "yearly-token", "$34.99")
            ),
            onSubscribe = {}
        )
    }
}
