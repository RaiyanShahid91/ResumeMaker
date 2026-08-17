package co.resume.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.resume.ai.AiClient
import co.resume.analytics.Analytics
import co.resume.data.local.entity.AiChatMessageEntity
import co.resume.data.repository.AiChatRepository
import co.resumeai.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SYSTEM_PROMPT = "You are a professional resume and cover letter writing assistant. Help the user write compelling content: objectives, job descriptions, project summaries, skills, achievements, and cover letter paragraphs. Keep responses concise and professional. When writing content, provide ready-to-use text they can copy directly into their document."

/**
 * Backs the in-editor AI assistant popup (see AiAssistantPopup.kt) — same underlying AiClient.chat
 * as the full-screen AiChatScreen, but the conversation is persisted per-document (resumeId XOR
 * coverLetterId) via AiChatRepository instead of living only in Compose state, so it survives the
 * popup being dismissed and reopened while the user stays on that resume/cover letter.
 */
@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatRepository: AiChatRepository
) : ViewModel() {

    private var boundResumeId: Long? = null
    private var boundCoverLetterId: Long? = null
    private var collectJob: Job? = null

    private val _messages = MutableStateFlow<List<AiChatMessageEntity>>(emptyList())
    val messages: StateFlow<List<AiChatMessageEntity>> = _messages.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    /** Idempotent — safe to call from a LaunchedEffect(resumeId, coverLetterId) on every recomposition. */
    fun bind(resumeId: Long?, coverLetterId: Long?) {
        if (collectJob != null && boundResumeId == resumeId && boundCoverLetterId == coverLetterId) return
        boundResumeId = resumeId
        boundCoverLetterId = coverLetterId
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            chatRepository.observeMessages(resumeId, coverLetterId).collect { _messages.value = it }
        }
    }

    fun send(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || _isThinking.value) return
        val resumeId = boundResumeId
        val coverLetterId = boundCoverLetterId
        viewModelScope.launch {
            chatRepository.addMessage(resumeId, coverLetterId, "user", trimmed)
            _isThinking.value = true
            Analytics.logEvent(Analytics.Event.AI_REQUESTED, mapOf(Analytics.Param.FEATURE to "editor_assistant"))
            val start = System.currentTimeMillis()
            try {
                val history = buildList {
                    add(AiClient.ChatMessage("system", SYSTEM_PROMPT))
                    _messages.value.forEach { add(AiClient.ChatMessage(it.role, it.content)) }
                    add(AiClient.ChatMessage("user", trimmed))
                }
                val reply = AiClient.chat(history)
                chatRepository.addMessage(resumeId, coverLetterId, "assistant", reply)
                Analytics.logEvent(
                    Analytics.Event.AI_SUCCESS,
                    mapOf(Analytics.Param.FEATURE to "editor_assistant", Analytics.Param.LATENCY_MS to System.currentTimeMillis() - start)
                )
            } catch (e: Exception) {
                val message = co.resume.ai.aiErrorMessage(e, context.getString(R.string.ai_chat_error_response))
                chatRepository.addMessage(resumeId, coverLetterId, "assistant", message)
                Analytics.logEvent(
                    Analytics.Event.AI_FAILED,
                    mapOf(Analytics.Param.FEATURE to "editor_assistant", Analytics.Param.ERROR_MESSAGE to e.message)
                )
                Analytics.logError("AiAssistantViewModel.send", e)
            } finally {
                _isThinking.value = false
            }
        }
    }
}
