package co.resume.data.repository

import co.resume.data.local.entity.CoverLetterEntity
import kotlinx.coroutines.flow.Flow

interface CoverLetterRepository {
    fun observeAllCoverLetters(): Flow<List<CoverLetterEntity>>
    fun observeCoverLetter(id: Long): Flow<CoverLetterEntity?>
    suspend fun createCoverLetter(): Long
    suspend fun updateCoverLetter(coverLetter: CoverLetterEntity)
    suspend fun deleteCoverLetter(id: Long)
}
