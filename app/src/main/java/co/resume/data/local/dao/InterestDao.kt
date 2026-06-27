package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.resume.data.local.entity.InterestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestDao {
    @Query("SELECT * FROM interests WHERE resumeId = :resumeId ORDER BY orderIndex ASC")
    fun observeByResumeId(resumeId: Long): Flow<List<InterestEntity>>

    @Insert
    suspend fun insertAll(items: List<InterestEntity>)

    @Query("DELETE FROM interests WHERE resumeId = :resumeId")
    suspend fun deleteByResumeId(resumeId: Long)

    @Transaction
    suspend fun replaceAll(resumeId: Long, items: List<InterestEntity>) {
        deleteByResumeId(resumeId)
        insertAll(items)
    }
}
