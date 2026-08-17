package co.resume.ui.component

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.data.local.entity.AiChatMessageEntity
import co.resume.ui.viewmodel.AiAssistantViewModel
import kotlinx.coroutines.launch

/**
 * Floating "AI assist" button meant to sit inside a resume/cover-letter editor screen (typically
 * anchored above the editor's own primary action, e.g. in a `floatingActionButton` Column) so the
 * user can reach the assistant without leaving the document they're filling in.
 */
@Composable
fun AiAssistantFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = Color.Transparent,
        elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(primary, tertiary))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = stringResource(R.string.ai_chat_title), tint = Color.White)
        }
    }
}

/**
 * The assistant itself, as a popup rather than a full navigation screen — a tall (not full-height)
 * rounded sheet over a dimmed scrim, so the editor underneath stays visibly present. Exactly one of
 * [resumeId]/[coverLetterId] should be non-null; the conversation persists under that id (see
 * AiAssistantViewModel/AiChatRepository) so closing and reopening the popup on the same document
 * keeps the history, while a different document starts its own separate thread.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantPopup(
    onDismiss: () -> Unit,
    resumeId: Long? = null,
    coverLetterId: Long? = null,
    viewModel: AiAssistantViewModel = hiltViewModel()
) {
    LaunchedEffect(resumeId, coverLetterId) { viewModel.bind(resumeId, coverLetterId) }

    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isThinking by viewModel.isThinking.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val copiedMsg = stringResource(R.string.ai_chat_copied)
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) listState.animateScrollToItem((messages.size - 1).coerceAtLeast(0))
    }

    fun send() {
        val text = inputText
        if (text.isBlank() || isThinking) return
        inputText = ""
        viewModel.send(text)
    }

    // A real Dialog (own window + scrim), not an inline composable — this is what actually makes
    // it "cover the screen but not the whole view": the Dialog's scrim dims the editor behind it,
    // while the content Box below is deliberately NOT fillMaxHeight() (88% + BottomCenter), so a
    // visible strip of the editor stays showing above the popup rather than a full-screen takeover.
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.BottomCenter) {
            AiAssistantPopupContent(
                messages = messages,
                isThinking = isThinking,
                inputText = inputText,
                onInputChange = { inputText = it },
                onSend = { scope.launch { send() } },
                onDismiss = onDismiss,
                listState = listState,
                onCopy = { text ->
                    clipboard.setText(AnnotatedString(text))
                    Toast.makeText(context, copiedMsg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiAssistantPopupContent(
    messages: List<AiChatMessageEntity>,
    isThinking: Boolean,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onCopy: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.88f)) {
        val primary = MaterialTheme.colorScheme.primary
        val tertiary = MaterialTheme.colorScheme.tertiary

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(MaterialTheme.colorScheme.background)
                .imePadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(primary, tertiary))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Column(modifier = Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(stringResource(R.string.ai_chat_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        stringResource(R.string.ai_chat_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.template_cd_close))
                }
            }

            if (!AiClient.isConfigured) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.ai_chat_not_configured),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Column
            }

            if (messages.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f).padding(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.ai_chat_welcome),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(messages, key = { _, m -> m.id }) { _, msg ->
                        AiAssistantMessageBubble(
                            message = msg,
                            gradient = listOf(primary, tertiary),
                            onCopy = { onCopy(msg.content) }
                        )
                    }

                    if (isThinking) {
                        item {
                            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.Bottom) {
                                ChatAvatarDot(gradient = listOf(primary, tertiary), icon = Icons.Filled.AutoAwesome)
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
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = { Text(stringResource(R.string.ai_chat_input_hint)) },
                    modifier = Modifier.weight(1f).shadow(elevation = 2.dp, shape = RoundedCornerShape(50)),
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
                val canSend = !isThinking && inputText.isNotBlank() && AiClient.isConfigured
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) Brush.linearGradient(listOf(primary, tertiary))
                            else Brush.linearGradient(listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surfaceVariant))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onSend, enabled = canSend) {
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
private fun AiAssistantMessageBubble(message: AiChatMessageEntity, gradient: List<Color>, onCopy: () -> Unit) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            ChatAvatarDot(gradient = gradient, icon = Icons.Filled.AutoAwesome)
            Spacer(Modifier.width(8.dp))
        }
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
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
                            Modifier.background(Brush.linearGradient(gradient))
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
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
            // Always-visible copy affordance (rather than only a long-press context menu) so the
            // user can grab an answer to paste into the document they're filling without having
            // to discover a hidden gesture first.
            if (!isUser) {
                IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = stringResource(R.string.ai_chat_copy),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        if (isUser) {
            Spacer(Modifier.width(8.dp))
            ChatAvatarDot(
                gradient = listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outlineVariant),
                icon = Icons.Filled.Person
            )
        }
    }
}

@Composable
private fun ChatAvatarDot(gradient: List<Color>, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier.size(28.dp).clip(CircleShape).background(Brush.linearGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
    }
}
