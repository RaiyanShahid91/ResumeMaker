package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.resume.data.local.entity.SkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {
    @Query("SELECT * FROM skills WHERE resumeId = :resumeId ORDER BY orderIndex ASC")
    fun observeByResumeId(resumeId: Long): Flow<List<SkillEntity>>

    @Insert
    suspend fun insertAll(items: List<SkillEntity>)

    @Query("DELETE FROM skills WHERE resumeId = :resumeId")
    suspend fun deleteByResumeId(resumeId: Long)

    @Transaction
    suspend fun replaceAll(resumeId: Long, items: List<SkillEntity>) {
        deleteByResumeId(resumeId)
        insertAll(items)
    }
}
