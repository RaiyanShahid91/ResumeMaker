package co.resume.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.resume.data.local.entity.ScannedDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedDocumentDao {
    @Query("SELECT * FROM scanned_documents ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ScannedDocumentEntity>>

    @Insert
    suspend fun insert(document: ScannedDocumentEntity): Long

    @Query("DELETE FROM scanned_documents WHERE id = :id")
    suspend fun delete(id: Long)
}
