package co.resume.ai

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.resumeai.BuildConfig
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds the Groq API key actually used by [AiClient] at call time. Starts out as the
 * locally-built fallback (from `local.properties` via [BuildConfig], empty in production once
 * that's removed) so AI still works before Remote Config has resolved, then gets overwritten by
 * [RemoteApiKeyFetcher] once the real key arrives from Firebase.
 *
 * Backed by Compose [mutableStateOf] rather than a plain `var` — every screen checks
 * [AiClient.isConfigured] once at composition time (see e.g. WorkExperienceSection's AI icon),
 * and without this, a key that resolves *after* that screen already composed would never show
 * the AI button: nothing would trigger a recomposition to notice the change. Reading `.apiKey`
 * from a `@Composable` now subscribes it to updates the normal Compose way.
 */
object RemoteApiKeyHolder {
    var apiKey: String by mutableStateOf(BuildConfig.GROQ_API_KEY)
}

// Reversible obfuscation only — NOT encryption. The app has to hold the real key in memory to
// call Groq directly, so this can't be made unbreakable client-side; what it actually buys is
// keeping the key out of committed build files and off the wire as a bare string, and letting it
// be rotated from the Firebase Console without shipping a new APK. A determined attacker with a
// decompiler can still recover XOR_KEY and reverse this — genuine hack-proofing needs the key to
// never reach the device at all (a backend proxy that calls Groq on the app's behalf).
private val XOR_KEY = byteArrayOf(0x5A, 0x91.toByte(), 0x2C, 0x74, 0xE3.toByte(), 0x1B, 0x88.toByte(), 0x4F)

private fun xor(bytes: ByteArray): ByteArray = ByteArray(bytes.size) { i -> (bytes[i].toInt() xor XOR_KEY[i % XOR_KEY.size].toInt()).toByte() }

/** Run this once (e.g. from a scratch script) to turn a real Groq key into the value you paste
 *  into Firebase Console → Remote Config as the `groq_api_key_obfuscated` parameter. */
fun obfuscateApiKey(plainKey: String): String = Base64.getEncoder().encodeToString(xor(plainKey.toByteArray(Charsets.UTF_8)))

private fun deobfuscateApiKey(encoded: String): String = String(xor(Base64.getDecoder().decode(encoded)), Charsets.UTF_8)

/**
 * Fetches `groq_api_key_obfuscated` from Firebase Remote Config and, once resolved, overwrites
 * [RemoteApiKeyHolder.apiKey] with the decoded value. Injected once into MainActivity so this
 * singleton (and its fetch) is created at app start; nothing else calls it directly.
 */
private const val TAG = "RemoteApiKeyFetcher"

@Singleton
class RemoteApiKeyFetcher @Inject constructor() {
    init {
        val remoteConfig = Firebase.remoteConfig
        // Debug builds always bypass Remote Config's fetch throttle — with the production
        // 1-hour interval, testing a just-published parameter change meant the SDK would
        // silently keep serving whatever it cached from an *earlier* fetch (e.g. from before the
        // parameter existed at all) for up to an hour, which looks exactly like "I set the value
        // but the app still says not configured." Chaining off setConfigSettingsAsync's own Task
        // (rather than firing fetchAndActivate right after, unawaited) also avoids a race where
        // the fetch could start before these settings actually took effect.
        val minFetchIntervalSeconds = if (BuildConfig.DEBUG) 0L else 3600L
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings { minimumFetchIntervalInSeconds = minFetchIntervalSeconds }
        ).addOnCompleteListener {
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Remote Config fetch failed", task.exception)
                    return@addOnCompleteListener
                }
                val obfuscated = remoteConfig.getString("groq_api_key_obfuscated")
                Log.i(TAG, "Fetch succeeded (activated=${task.result}), groq_api_key_obfuscated length=${obfuscated.length}")
                if (obfuscated.isBlank()) {
                    Log.w(TAG, "groq_api_key_obfuscated is empty — check the parameter is Published in Firebase Console, not just saved as a draft")
                    return@addOnCompleteListener
                }
                runCatching { deobfuscateApiKey(obfuscated) }
                    .onSuccess { key ->
                        if (key.isNotBlank()) {
                            RemoteApiKeyHolder.apiKey = key
                            Log.i(TAG, "Applied Groq key from Remote Config (length=${key.length})")
                        }
                    }
                    .onFailure { Log.w(TAG, "Failed to decode groq_api_key_obfuscated — value may not be valid obfuscated output", it) }
            }
        }
    }
}
