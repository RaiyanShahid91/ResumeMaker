package co.resume.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val VALID_LAYOUT_IDS = setOf(
    "ats_safe", "photo_header", "sidebar_left", "sidebar_right",
    "timeline", "color_block_header", "minimal_modern", "compact_dense",
)
private val VALID_THEME_IDS = setOf(
    "slate_classic", "sapphire", "emerald", "crimson", "charcoal_gold", "plum", "teal_breeze",
)

/**
 * Verifies MIGRATION_4_5 maps every legacy templateId (1-18) to a valid, non-null (layoutId,
 * themeId) pair — the required check called out in the template-rewrite plan before this
 * migration ships, since it's a destructive/irreversible column rebuild.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate4To5_mapsEveryLegacyTemplateIdToAValidLayoutAndTheme() {
        helper.createDatabase(TEST_DB, 4).apply {
            for (templateId in 1..18) {
                execSQL(
                    """
                    INSERT INTO resumes (
                        legacyResumeId, name, designation, email, phone, address, objective,
                        declaration, declarationPlace, declarationDate, profilePhotoPath, signaturePath,
                        templateId, fontFamilyIndex, colorOneIndex, colorTwoIndex, accentColorHex,
                        sectionOrder, hiddenSections, isDeleted, deletedAt, createdAt, updatedAt
                    ) VALUES (
                        NULL, 'Test User $templateId', '', '', '', '', '', '', '', '', NULL, NULL,
                        $templateId, 0, 0, 0, NULL, 'objective', '', 0, NULL, 0, 0
                    )
                    """.trimIndent(),
                )
            }
            close()
        }

        val migrated = helper.runMigrationsAndValidate(TEST_DB, 5, true, AppDatabase.MIGRATION_4_5)
        val cursor = migrated.query("SELECT layoutId, themeId FROM resumes")
        var rowCount = 0
        while (cursor.moveToNext()) {
            rowCount++
            val layoutId = cursor.getString(cursor.getColumnIndexOrThrow("layoutId"))
            val themeId = cursor.getString(cursor.getColumnIndexOrThrow("themeId"))
            assertTrue("layoutId '$layoutId' must be a known layout", layoutId in VALID_LAYOUT_IDS)
            assertTrue("themeId '$themeId' must be a known theme", themeId in VALID_THEME_IDS)
        }
        cursor.close()
        assertTrue("expected all 18 legacy rows to survive the migration", rowCount == 18)
    }

    companion object {
        private const val TEST_DB = "migration-test"
    }
}
