package co.resume.ui.viewmodel

import androidx.lifecycle.ViewModel
import co.resume.ai.AiAccessRepository
import co.resume.ai.RemoteApiKeyFetcher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Exists only to force [RemoteApiKeyFetcher] and [AiAccessRepository] into existence the moment
 * the Dashboard (home screen) is reached — that's what actually starts the Remote Config fetch
 * for the Groq key and the per-user AI-disabled listener. Neither is called directly; injecting
 * them is the trigger.
 */
@HiltViewModel
class AiBootstrapViewModel @Inject constructor(
    remoteApiKeyFetcher: RemoteApiKeyFetcher,
    aiAccessRepository: AiAccessRepository
) : ViewModel()
