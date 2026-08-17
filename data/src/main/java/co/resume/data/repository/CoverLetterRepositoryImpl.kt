package co.resume.data.repository

import co.resume.data.local.dao.CoverLetterDao
import co.resume.data.local.entity.CoverLetterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CoverLetterRepositoryImpl @Inject constructor(
    private val coverLetterDao: CoverLetterDao
) : CoverLetterRepository {

    override fun observeAllCoverLetters(): Flow<List<CoverLetterEntity>> = coverLetterDao.observeAll()

    override fun observeCoverLetter(id: Long): Flow<CoverLetterEntity?> = coverLetterDao.observeById(id)

    override suspend fun createCoverLetter(): Long = coverLetterDao.insert(CoverLetterEntity())

    override suspend fun updateCoverLetter(coverLetter: CoverLetterEntity) {
        coverLetterDao.update(coverLetter.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteCoverLetter(id: Long) = coverLetterDao.delete(id)
}
