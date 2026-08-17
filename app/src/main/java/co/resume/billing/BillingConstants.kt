package co.resume.billing

/**
 * Product/base-plan IDs must match exactly what's configured in Play Console →
 * Monetize → Products → Subscriptions. Create ONE subscription product ("premium")
 * with TWO base plans ("monthly" and "yearly") rather than two separate products —
 * that's what lets Play show them side-by-side as plan choices for the same entitlement.
 */
object BillingConstants {
    const val PREMIUM_PRODUCT_ID = "premium"
    const val MONTHLY_BASE_PLAN_ID = "monthly"
    const val YEARLY_BASE_PLAN_ID = "yearly"
}
