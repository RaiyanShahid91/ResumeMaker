package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.resume.data.local.entity.WorkExperienceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkExperienceDao {
    @Query("SELECT * FROM work_experience WHERE resumeId = :resumeId ORDER BY orderIndex ASC")
    fun observeByResumeId(resumeId: Long): Flow<List<WorkExperienceEntity>>

    @Insert
    suspend fun insertAll(items: List<WorkExperienceEntity>)

    @Query("DELETE FROM work_experience WHERE resumeId = :resumeId")
    suspend fun deleteByResumeId(resumeId: Long)

    @Transaction
    suspend fun replaceAll(resumeId: Long, items: List<WorkExperienceEntity>) {
        deleteByResumeId(resumeId)
        insertAll(items)
    }
}
