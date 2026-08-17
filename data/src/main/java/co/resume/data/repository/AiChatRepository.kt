package co.resume.data.repository

import co.resume.data.local.dao.AiChatDao
import co.resume.data.local.entity.AiChatMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AiChatRepository {
    fun observeMessages(resumeId: Long?, coverLetterId: Long?): Flow<List<AiChatMessageEntity>>
    suspend fun addMessage(resumeId: Long?, coverLetterId: Long?, role: String, content: String)
}

class AiChatRepositoryImpl @Inject constructor(
    private val dao: AiChatDao
) : AiChatRepository {

    override fun observeMessages(resumeId: Long?, coverLetterId: Long?): Flow<List<AiChatMessageEntity>> = when {
        resumeId != null -> dao.observeForResume(resumeId)
        coverLetterId != null -> dao.observeForCoverLetter(coverLetterId)
        else -> throw IllegalArgumentException("Either resumeId or coverLetterId must be set")
    }

    override suspend fun addMessage(resumeId: Long?, coverLetterId: Long?, role: String, content: String) {
        dao.insert(AiChatMessageEntity(resumeId = resumeId, coverLetterId = coverLetterId, role = role, content = content))
    }
}
