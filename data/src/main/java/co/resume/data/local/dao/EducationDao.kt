package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.resume.data.local.entity.EducationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EducationDao {
    @Query("SELECT * FROM education WHERE resumeId = :resumeId ORDER BY orderIndex ASC")
    fun observeByResumeId(resumeId: Long): Flow<List<EducationEntity>>

    @Insert
    suspend fun insertAll(items: List<EducationEntity>)

    @Query("DELETE FROM education WHERE resumeId = :resumeId")
    suspend fun deleteByResumeId(resumeId: Long)

    @Transaction
    suspend fun replaceAll(resumeId: Long, items: List<EducationEntity>) {
        deleteByResumeId(resumeId)
        insertAll(items)
    }
}
