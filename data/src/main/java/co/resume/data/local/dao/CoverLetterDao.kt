package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import co.resume.data.local.entity.CoverLetterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoverLetterDao {
    @Query("SELECT * FROM cover_letters ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<CoverLetterEntity>>

    @Query("SELECT * FROM cover_letters WHERE id = :id")
    fun observeById(id: Long): Flow<CoverLetterEntity?>

    @Insert
    suspend fun insert(coverLetter: CoverLetterEntity): Long

    @Update
    suspend fun update(coverLetter: CoverLetterEntity)

    @Query("DELETE FROM cover_letters WHERE id = :id")
    suspend fun delete(id: Long)
}
