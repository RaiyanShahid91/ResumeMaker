package co.resume.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object AiClient {

    val isConfigured: Boolean
        get() = RemoteApiKeyHolder.apiKey.isNotBlank()

    private val http by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    data class ChatMessage(val role: String, val content: String)

    suspend fun chat(messages: List<ChatMessage>): String {
        val messagesArray = JSONArray()
        messages.forEach { msg ->
            messagesArray.put(JSONObject().put("role", msg.role).put("content", msg.content))
        }
        val body = JSONObject().apply {
            put("model", "llama-3.1-8b-instant")
            put("messages", messagesArray)
            put("max_tokens", 600)
            put("temperature", 0.7)
        }
        return callGroq(body)
    }

    suspend fun generate(prompt: String): String {
        val body = JSONObject().apply {
            put("model", "llama-3.1-8b-instant")
            put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", prompt)))
            put("max_tokens", 400)
            put("temperature", 0.7)
        }
        return callGroq(body)
    }

    /**
     * Like [generate], but asks Groq for JSON-mode output (`response_format: json_object`) —
     * every other caller here wants a short piece of prose, so this is split out rather than
     * adding a flag to [generate]: a much larger [maxTokens] (structured multi-section data is
     * far longer than a suggested skill list or a paragraph) and a low temperature (extraction
     * should be deterministic, not creative) would be wrong defaults for those existing callers.
     */
    suspend fun generateJson(prompt: String, maxTokens: Int = 3000): String {
        val body = JSONObject().apply {
            put("model", "llama-3.1-8b-instant")
            put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", prompt)))
            put("max_tokens", maxTokens)
            put("temperature", 0.2)
            put("response_format", JSONObject().put("type", "json_object"))
        }
        return callGroq(body)
    }

    /** Single choke point every AI request goes through — this is what makes both the per-user
     *  kill switch ([AiAccessGate]) and failure reporting ([AiFailureReporter]) apply uniformly
     *  without every caller having to remember to check/report separately. */
    private suspend fun callGroq(body: JSONObject): String = withContext(Dispatchers.IO) {
        if (AiAccessGate.isDisabled) throw AiDisabledException()

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .post(body.toString().toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer ${RemoteApiKeyHolder.apiKey}")
            .build()

        val response = try {
            http.newCall(request).execute()
        } catch (e: IOException) {
            AiFailureReporter.report(e.message ?: "Network error calling Groq")
            throw e
        }
        val responseText = response.body?.string() ?: run {
            AiFailureReporter.report("Empty response from server", response.code)
            throw Exception("Empty response from server")
        }

        if (!response.isSuccessful) {
            val msg = runCatching {
                JSONObject(responseText).getJSONObject("error").getString("message")
            }.getOrDefault(responseText)
            AiFailureReporter.report(msg, response.code)
            throw Exception("AI error ${response.code}: $msg")
        }

        JSONObject(responseText)
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()
    }
}
