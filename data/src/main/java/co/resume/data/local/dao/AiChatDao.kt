package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.resume.data.local.entity.AiChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiChatDao {

    @Query("SELECT * FROM ai_chat_messages WHERE resumeId = :resumeId ORDER BY createdAt ASC")
    fun observeForResume(resumeId: Long): Flow<List<AiChatMessageEntity>>

    @Query("SELECT * FROM ai_chat_messages WHERE coverLetterId = :coverLetterId ORDER BY createdAt ASC")
    fun observeForCoverLetter(coverLetterId: Long): Flow<List<AiChatMessageEntity>>

    @Insert
    suspend fun insert(message: AiChatMessageEntity): Long
}
