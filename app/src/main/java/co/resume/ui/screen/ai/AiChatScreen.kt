package co.resume.ui.screen.ai

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.analytics.Analytics
import co.resume.ui.theme.ResumeBuilderTheme
import kotlinx.coroutines.launch

private const val SYSTEM_PROMPT = "You are a professional resume writing assistant. Help the user write compelling resume content: objectives, job descriptions, project summaries, skills, and achievements. Keep responses concise and professional. When writing content, provide ready-to-use text they can copy directly into their resume."

private data class Message(val role: String, val content: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AiChatScreen(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val welcomeText = stringResource(R.string.ai_chat_welcome)
    val messages = remember { mutableStateListOf(Message("assistant", welcomeText)) }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }
    var menuOpenIndex by remember { mutableIntStateOf(-1) }
    val errorResponseMsg = stringResource(R.string.ai_chat_error_response)
    val copiedMsg = stringResource(R.string.ai_chat_copied)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isBlank() || isThinking) return
        inputText = ""
        messages.add(Message("user", text))
        isThinking = true
        Analytics.logEvent(Analytics.Event.AI_REQUESTED, mapOf(Analytics.Param.FEATURE to "chat"))
        val start = System.currentTimeMillis()
        scope.launch {
            try {
                val history = buildList {
                    add(AiClient.ChatMessage("system", SYSTEM_PROMPT))
                    messages.forEach { add(AiClient.ChatMessage(it.role, it.content)) }
                }
                val reply = AiClient.chat(history)
                messages.add(Message("assistant", reply))
                Analytics.logEvent(
                    Analytics.Event.AI_SUCCESS,
                    mapOf(Analytics.Param.FEATURE to "chat", Analytics.Param.LATENCY_MS to System.currentTimeMillis() - start)
                )
            } catch (e: Exception) {
                messages.add(Message("assistant", co.resume.ai.aiErrorMessage(e, errorResponseMsg)))
                Analytics.logEvent(
                    Analytics.Event.AI_FAILED,
                    mapOf(Analytics.Param.FEATURE to "chat", Analytics.Param.ERROR_MESSAGE to e.message)
                )
                Analytics.logError("AiChatScreen.chat", e)
            } finally {
                isThinking = false
            }
        }
    }

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(primary, tertiary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Column(modifier = Modifier.padding(start = 10.dp)) {
                            Text(stringResource(R.string.ai_chat_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                stringResource(R.string.ai_chat_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                }
            )
        }
    ) { padding ->
        if (!AiClient.isConfigured) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.ai_chat_not_configured),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(messages) { index, msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (!isUser) {
                            ChatAvatar(gradient = listOf(primary, tertiary), icon = Icons.Filled.AutoAwesome)
                            Spacer(Modifier.width(8.dp))
                        }
                        Box {
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 18.dp,
                                            topEnd = 18.dp,
                                            bottomStart = if (isUser) 18.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 18.dp
                                        )
                                    )
                                    .then(
                                        if (isUser) {
                                            Modifier.background(Brush.linearGradient(listOf(primary, tertiary)))
                                        } else {
                                            Modifier
                                                .background(MaterialTheme.colorScheme.surface)
                                                .border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                                    RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
                                                )
                                        }
                                    )
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = { menuOpenIndex = index }
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    msg.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isUser) Color.White
                                            else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            DropdownMenu(
                                expanded = menuOpenIndex == index,
                                onDismissRequest = { menuOpenIndex = -1 }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.ai_chat_copy)) },
                                    onClick = {
                                        clipboard.setText(AnnotatedString(msg.content))
                                        Toast.makeText(context, copiedMsg, Toast.LENGTH_SHORT).show()
                                        menuOpenIndex = -1
                                    }
                                )
                                if (isUser) {
                                    DropdownMenuItem(
                                        text = { Text(stringResource(R.string.cd_edit)) },
                                        onClick = {
                                            inputText = msg.content
                                            menuOpenIndex = -1
                                        }
                                    )
                                }
                            }
                        }
                        if (isUser) {
                            Spacer(Modifier.width(8.dp))
                            ChatAvatar(
                                gradient = listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outlineVariant),
                                icon = Icons.Filled.Person
                            )
                        }
                    }
                }

                if (isThinking) {
                    item {
                        Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Bottom) {
                            ChatAvatar(gradient = listOf(primary, tertiary), icon = Icons.Filled.AutoAwesome)
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 4.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text(stringResource(R.string.ai_chat_input_hint)) },
                    modifier = Modifier
                        .weight(1f)
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(50)),
                    shape = RoundedCornerShape(50),
                    maxLines = 4,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                Spacer(Modifier.width(10.dp))
                val canSend = !isThinking && inputText.isNotBlank()
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) Brush.linearGradient(listOf(primary, tertiary))
                            else Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { sendMessage() }, enabled = canSend) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(R.string.ai_chat_send),
                            tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatAvatar(gradient: List<Color>, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
    }
}

// AiClient.isConfigured depends on BuildConfig.GROQ_API_KEY, which is empty in most local/preview
// builds — the screen then renders its "not configured" empty state rather than the chat UI.
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
private fun AiChatScreenPreview() {
    ResumeBuilderTheme { AiChatScreen(onBack = {}) }
}
