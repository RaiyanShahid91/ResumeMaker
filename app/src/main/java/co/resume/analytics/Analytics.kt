package co.resume.analytics

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object Analytics {

    object Event {
        const val SCREEN_VIEW = "screen_view"
        const val ONBOARDING_COMPLETED = "onboarding_completed"
        const val LANGUAGE_CHANGED = "language_changed"
        const val RESUME_CREATED = "resume_created"
        const val RESUME_DELETED = "resume_deleted"
        const val TEMPLATE_SELECTED = "template_selected"
        const val SECTION_SAVED = "section_saved"
        const val AI_REQUESTED = "ai_requested"
        const val AI_SUCCESS = "ai_success"
        const val AI_FAILED = "ai_failed"
        const val SCAN_STARTED = "scan_started"
        const val SCAN_COMPLETED = "scan_completed"
        const val DOCUMENT_EXPORTED = "document_exported"
        const val DOCUMENT_SHARED = "document_shared"
        const val DOCUMENT_DELETED = "document_deleted"
        const val COVER_LETTER_CREATED = "cover_letter_created"
        const val COVER_LETTER_TEMPLATE_SELECTED = "cover_letter_template_selected"
        const val CONVERT_COMPLETED = "convert_completed"
        const val CONVERT_FAILED = "convert_failed"
    }

    object Param {
        const val SCREEN_NAME = "screen_name"
        const val LANGUAGE_CODE = "language_code"
        const val TEMPLATE_ID = "template_id"
        const val SECTION_TYPE = "section_type"
        const val FEATURE = "feature"
        const val LATENCY_MS = "latency_ms"
        const val ERROR_MESSAGE = "error_message"
        const val PAGE_COUNT = "page_count"
        const val FORMAT = "format"
    }

    private var analytics: FirebaseAnalytics? = null

    @JvmStatic
    fun init(context: Context) {
        analytics = FirebaseAnalytics.getInstance(context.applicationContext)
    }

    fun logScreenView(routeTemplate: String) {
        logEvent(Event.SCREEN_VIEW, mapOf(Param.SCREEN_NAME to routeTemplate))
    }

    fun logEvent(name: String, params: Map<String, Any?> = emptyMap()) {
        val bundle = android.os.Bundle()
        params.forEach { (key, value) ->
            when (value) {
                null -> Unit
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putString(key, value.toString())
                else -> bundle.putString(key, value.toString())
            }
        }
        analytics?.logEvent(name, bundle)
    }

    fun logError(tag: String, throwable: Throwable) {
        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("error_tag", tag)
            recordException(throwable)
        }
    }
}
