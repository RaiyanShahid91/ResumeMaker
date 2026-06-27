package co.resume.ai

import co.resumeai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object AiClient {

    val isConfigured: Boolean
        get() = BuildConfig.GROQ_API_KEY.isNotBlank()

    private val http = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    data class ChatMessage(val role: String, val content: String)

    suspend fun chat(messages: List<ChatMessage>): String = withContext(Dispatchers.IO) {
        val messagesArray = JSONArray()
        messages.forEach { msg ->
            messagesArray.put(JSONObject().put("role", msg.role).put("content", msg.content))
        }
        val body = JSONObject().apply {
            put("model", "llama-3.1-8b-instant")
            put("messages", messagesArray)
            put("max_tokens", 600)
            put("temperature", 0.7)
        }.toString()

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .post(body.toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
            .build()

        val response = http.newCall(request).execute()
        val responseText = response.body?.string() ?: throw Exception("Empty response from server")

        if (!response.isSuccessful) {
            val msg = runCatching {
                JSONObject(responseText).getJSONObject("error").getString("message")
            }.getOrDefault(responseText)
            throw Exception("AI error ${response.code}: $msg")
        }

        JSONObject(responseText)
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()
    }

    suspend fun generate(prompt: String): String = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("model", "llama-3.1-8b-instant")
            put("messages", JSONArray().put(
                JSONObject().put("role", "user").put("content", prompt)
            ))
            put("max_tokens", 400)
            put("temperature", 0.7)
        }.toString()

        val request = Request.Builder()
            .url("https://api.groq.com/openai/v1/chat/completions")
            .post(body.toRequestBody("application/json".toMediaType()))
            .header("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
            .build()

        val response = http.newCall(request).execute()
        val responseText = response.body?.string() ?: throw Exception("Empty response from server")

        if (!response.isSuccessful) {
            val msg = runCatching {
                JSONObject(responseText).getJSONObject("error").getString("message")
            }.getOrDefault(responseText)
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
