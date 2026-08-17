package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.resume.data.local.entity.HobbyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HobbyDao {
    @Query("SELECT * FROM hobbies WHERE resumeId = :resumeId ORDER BY orderIndex ASC")
    fun observeByResumeId(resumeId: Long): Flow<List<HobbyEntity>>

    @Insert
    suspend fun insertAll(items: List<HobbyEntity>)

    @Query("DELETE FROM hobbies WHERE resumeId = :resumeId")
    suspend fun deleteByResumeId(resumeId: Long)

    @Transaction
    suspend fun replaceAll(resumeId: Long, items: List<HobbyEntity>) {
        deleteByResumeId(resumeId)
        insertAll(items)
    }
}
