package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.resume.ai.RemoteApiKeyFetcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Exists only to force [RemoteApiKeyFetcher] into existence the moment the Dashboard (home
 * screen) is reached — that's what actually starts the Remote Config fetch for the Groq key.
 * It's not called directly; injecting it is the trigger.
 */
@HiltViewModel
class AiBootstrapViewModel @Inject constructor(
    remoteApiKeyFetcher: RemoteApiKeyFetcher
) : ViewModel()
