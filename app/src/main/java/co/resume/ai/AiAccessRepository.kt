package co.resume.ai

import co.resume.auth.AuthRepository
import co.resume.auth.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keeps [AiAccessGate.isDisabled] in sync with the signed-in user's `aiDisabled` field in
 * Firestore. Injected once into MainActivity purely to force this singleton (and its listener)
 * into existence at app start — nothing calls methods on it directly afterward.
 */
@Singleton
class AiAccessRepository @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            authRepository.currentUser.collectLatest { user ->
                val uid = if (user != null) authRepository.currentUid else null
                if (uid == null) {
                    AiAccessGate.isDisabled = false
                    return@collectLatest
                }
                userProfileRepository.observeAiDisabled(uid).collect { disabled ->
                    AiAccessGate.isDisabled = disabled
                }
            }
        }
    }
}
