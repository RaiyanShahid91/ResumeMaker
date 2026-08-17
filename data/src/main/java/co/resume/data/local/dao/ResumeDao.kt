package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.local.entity.ResumeWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumeDao {

    @Query("SELECT * FROM resumes WHERE isDeleted = 0 ORDER BY sortOrder ASC, updatedAt DESC")
    fun observeAll(): Flow<List<ResumeEntity>>

    @Transaction
    @Query("SELECT * FROM resumes WHERE isDeleted = 0 ORDER BY sortOrder ASC, updatedAt DESC")
    fun observeAllWithDetails(): Flow<List<ResumeWithDetails>>

    @Query("SELECT * FROM resumes WHERE isDeleted = 1 ORDER BY deletedAt DESC")
    fun observeDeleted(): Flow<List<ResumeEntity>>

    @Query("SELECT * FROM resumes WHERE id = :id")
    fun observeById(id: Long): Flow<ResumeEntity?>

    @Transaction
    @Query("SELECT * FROM resumes WHERE id = :id")
    fun observeWithDetails(id: Long): Flow<ResumeWithDetails?>

    @Insert
    suspend fun insert(resume: ResumeEntity): Long

    @Update
    suspend fun update(resume: ResumeEntity)

    @Delete
    suspend fun delete(resume: ResumeEntity)

    @Query("UPDATE resumes SET updatedAt = :timestamp WHERE id = :id")
    suspend fun touch(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE resumes SET isDeleted = 1, deletedAt = :timestamp WHERE id = :id")
    suspend fun softDelete(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE resumes SET isDeleted = 0, deletedAt = NULL WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("DELETE FROM resumes WHERE isDeleted = 1 AND deletedAt < :olderThan")
    suspend fun purgeDeletedOlderThan(olderThan: Long)

    @Query("SELECT * FROM resumes WHERE id = :id")
    suspend fun getById(id: Long): ResumeEntity?

    @Transaction
    @Query("SELECT * FROM resumes WHERE id = :id")
    suspend fun getWithDetails(id: Long): ResumeWithDetails?

    @Query("UPDATE resumes SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int)

    /** Assigns each id its list position as the new sortOrder — the whole visible list is always
     *  passed in (see ResumeRepository.reorderResumes), so this fully re-numbers it in one go
     *  rather than trying to patch just the two rows that visually swapped. */
    @Transaction
    suspend fun reorder(orderedIds: List<Long>) {
        orderedIds.forEachIndexed { index, id -> updateSortOrder(id, index) }
    }
}
