package co.resume.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration
import co.resume.data.local.dao.AchievementDao
import co.resume.data.local.dao.AiChatDao
import co.resume.data.local.dao.CoverLetterDao
import co.resume.data.local.dao.EducationDao
import co.resume.data.local.dao.HobbyDao
import co.resume.data.local.dao.InterestDao
import co.resume.data.local.dao.LanguageDao
import co.resume.data.local.dao.ProjectDao
import co.resume.data.local.dao.ResumeDao
import co.resume.data.local.dao.ScannedDocumentDao
import co.resume.data.local.dao.SkillDao
import co.resume.data.local.dao.WorkExperienceDao
import co.resume.data.local.entity.AchievementEntity
import co.resume.data.local.entity.AiChatMessageEntity
import co.resume.data.local.entity.CoverLetterEntity
import co.resume.data.local.entity.EducationEntity
import co.resume.data.local.entity.HobbyEntity
import co.resume.data.local.entity.InterestEntity
import co.resume.data.local.entity.LanguageEntity
import co.resume.data.local.entity.ProjectEntity
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.local.entity.ScannedDocumentEntity
import co.resume.data.local.entity.SkillEntity
import co.resume.data.local.entity.WorkExperienceEntity

@Database(
    entities = [
        ResumeEntity::class,
        EducationEntity::class,
        WorkExperienceEntity::class,
        SkillEntity::class,
        ProjectEntity::class,
        AchievementEntity::class,
        LanguageEntity::class,
        InterestEntity::class,
        HobbyEntity::class,
        ScannedDocumentEntity::class,
        CoverLetterEntity::class,
        AiChatMessageEntity::class
    ],
    version = 9,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resumeDao(): ResumeDao
    abstract fun coverLetterDao(): CoverLetterDao
    abstract fun educationDao(): EducationDao
    abstract fun workExperienceDao(): WorkExperienceDao
    abstract fun skillDao(): SkillDao
    abstract fun projectDao(): ProjectDao
    abstract fun achievementDao(): AchievementDao
    abstract fun languageDao(): LanguageDao
    abstract fun interestDao(): InterestDao
    abstract fun hobbyDao(): HobbyDao
    abstract fun scannedDocumentDao(): ScannedDocumentDao
    abstract fun aiChatDao(): AiChatDao

    companion object {
        const val DATABASE_NAME = "resume_maker.db"

        /** Adds the scanned_documents table for the document-scanner feature. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `scanned_documents` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `title` TEXT NOT NULL,
                        `pdfPath` TEXT NOT NULL,
                        `pageCount` INTEGER NOT NULL,
                        `ocrText` TEXT NOT NULL,
                        `needsReview` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        /** Adds pagePaths to scanned_documents so each scanned page's image is kept, not just the merged PDF. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `scanned_documents` ADD COLUMN `pagePaths` TEXT NOT NULL DEFAULT ''")
            }
        }

        /** Adds the cover_letters table for the cover-letter generator feature. */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `cover_letters` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `title` TEXT NOT NULL,
                        `recipientName` TEXT NOT NULL,
                        `companyName` TEXT NOT NULL,
                        `jobTitle` TEXT NOT NULL,
                        `senderName` TEXT NOT NULL,
                        `senderEmail` TEXT NOT NULL,
                        `senderPhone` TEXT NOT NULL,
                        `date` TEXT NOT NULL,
                        `salutation` TEXT NOT NULL,
                        `bodyText` TEXT NOT NULL,
                        `closing` TEXT NOT NULL,
                        `templateId` INTEGER NOT NULL,
                        `accentColorHex` TEXT,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        /**
         * Replaces the old templateId(Int)/fontFamilyIndex/colorOneIndex/colorTwoIndex scheme with
         * layoutId/themeId (String) from the new 8-layout x theming system. accentColorHex,
         * sectionOrder and hiddenSections are unchanged — accentColorHex is now interpreted as an
         * optional per-resume override on top of the theme's accent color rather than a raw palette
         * index. SQLite (pre-3.35, still possible on minSdk 24 devices) can't DROP COLUMN, so this
         * rebuilds the table: create-new / copy-with-mapped-columns / drop-old / rename.
         *
         * The old templateId 1-18 -> (layoutId, themeId) mapping below is a hand-curated best-fit,
         * not a lossless conversion — every old id is guaranteed to land on a valid, non-null pair
         * (see AppDatabaseMigrationTest), but the exact old visual design is not reproduced exactly.
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `resumes_new` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `legacyResumeId` TEXT,
                        `name` TEXT NOT NULL,
                        `designation` TEXT NOT NULL,
                        `email` TEXT NOT NULL,
                        `phone` TEXT NOT NULL,
                        `address` TEXT NOT NULL,
                        `objective` TEXT NOT NULL,
                        `declaration` TEXT NOT NULL,
                        `declarationPlace` TEXT NOT NULL,
                        `declarationDate` TEXT NOT NULL,
                        `profilePhotoPath` TEXT,
                        `signaturePath` TEXT,
                        `layoutId` TEXT NOT NULL DEFAULT 'ats_safe',
                        `themeId` TEXT NOT NULL DEFAULT 'slate_classic',
                        `accentColorHex` TEXT,
                        `sectionOrder` TEXT NOT NULL,
                        `hiddenSections` TEXT NOT NULL,
                        `isDeleted` INTEGER NOT NULL,
                        `deletedAt` INTEGER,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO `resumes_new` (
                        id, legacyResumeId, name, designation, email, phone, address, objective,
                        declaration, declarationPlace, declarationDate, profilePhotoPath, signaturePath,
                        layoutId, themeId, accentColorHex, sectionOrder, hiddenSections, isDeleted,
                        deletedAt, createdAt, updatedAt
                    )
                    SELECT
                        id, legacyResumeId, name, designation, email, phone, address, objective,
                        declaration, declarationPlace, declarationDate, profilePhotoPath, signaturePath,
                        CASE templateId
                            WHEN 1 THEN 'photo_header' WHEN 2 THEN 'minimal_modern' WHEN 3 THEN 'ats_safe'
                            WHEN 4 THEN 'color_block_header' WHEN 5 THEN 'sidebar_left' WHEN 6 THEN 'color_block_header'
                            WHEN 7 THEN 'minimal_modern' WHEN 8 THEN 'compact_dense' WHEN 9 THEN 'ats_safe'
                            WHEN 10 THEN 'compact_dense' WHEN 11 THEN 'sidebar_left' WHEN 12 THEN 'sidebar_right'
                            WHEN 13 THEN 'ats_safe' WHEN 14 THEN 'minimal_modern' WHEN 15 THEN 'sidebar_right'
                            WHEN 16 THEN 'timeline' WHEN 17 THEN 'ats_safe' WHEN 18 THEN 'sidebar_right'
                            ELSE 'ats_safe'
                        END,
                        CASE templateId
                            WHEN 1 THEN 'sapphire' WHEN 2 THEN 'slate_classic' WHEN 3 THEN 'slate_classic'
                            WHEN 4 THEN 'plum' WHEN 5 THEN 'sapphire' WHEN 6 THEN 'sapphire'
                            WHEN 7 THEN 'teal_breeze' WHEN 8 THEN 'slate_classic' WHEN 9 THEN 'emerald'
                            WHEN 10 THEN 'emerald' WHEN 11 THEN 'charcoal_gold' WHEN 12 THEN 'plum'
                            WHEN 13 THEN 'sapphire' WHEN 14 THEN 'sapphire' WHEN 15 THEN 'emerald'
                            WHEN 16 THEN 'crimson' WHEN 17 THEN 'crimson' WHEN 18 THEN 'slate_classic'
                            ELSE 'slate_classic'
                        END,
                        accentColorHex, sectionOrder, hiddenSections, isDeleted, deletedAt, createdAt, updatedAt
                    FROM `resumes`
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE `resumes`")
                db.execSQL("ALTER TABLE `resumes_new` RENAME TO `resumes`")
            }
        }

        /** Adds sortOrder for drag-to-reorder in "My Resumes" — see ResumeEntity.sortOrder. */
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `resumes` ADD COLUMN `sortOrder` INTEGER NOT NULL DEFAULT 0")
            }
        }

        /** Adds ai_chat_messages for the in-editor AI assistant popup — see AiChatMessageEntity. */
        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `ai_chat_messages` (
                        `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                        `resumeId` INTEGER,
                        `coverLetterId` INTEGER,
                        `role` TEXT NOT NULL,
                        `content` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        FOREIGN KEY(`resumeId`) REFERENCES `resumes`(`id`) ON DELETE CASCADE,
                        FOREIGN KEY(`coverLetterId`) REFERENCES `cover_letters`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_ai_chat_messages_resumeId` ON `ai_chat_messages` (`resumeId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_ai_chat_messages_coverLetterId` ON `ai_chat_messages` (`coverLetterId`)")
            }
        }

        /**
         * Adds `kind`/`imagePath` to scanned_documents so the PDF-converter toolkit (image-to-PDF,
         * merge, compress, PDF-to-JPG) can share the same table/UI as document scans — see
         * ScannedDocumentEntity.DocumentKind.
         */
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `scanned_documents` ADD COLUMN `kind` TEXT NOT NULL DEFAULT 'scan'")
                db.execSQL("ALTER TABLE `scanned_documents` ADD COLUMN `imagePath` TEXT")
            }
        }

        /** Adds per-resume font family + size overrides — see ResumeEntity.fontFamilyId/fontSizeScale. */
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `resumes` ADD COLUMN `fontFamilyId` TEXT")
                db.execSQL("ALTER TABLE `resumes` ADD COLUMN `fontSizeScale` REAL")
            }
        }
    }
}
