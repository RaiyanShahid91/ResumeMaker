package co.resume.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val DEFAULT_SECTION_ORDER =
    "objective,education,work_experience,skills,projects,achievements,languages,declaration"

@Entity(tableName = "resumes")
data class ResumeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val legacyResumeId: String? = null,
    val name: String = "",
    val designation: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val objective: String = "",
    val declaration: String = "",
    val declarationPlace: String = "",
    val declarationDate: String = "",
    val profilePhotoPath: String? = null,
    val signaturePath: String? = null,
    val layoutId: String = "ats_safe",
    val themeId: String = "slate_classic",
    /** Optional per-resume override on top of the theme's accent color; null uses the theme's own accent. */
    val accentColorHex: String? = null,
    /** Optional per-resume override on top of the theme's own font pairing; null uses the theme's
     *  own font. See FontCatalog.resolve in the templates module. */
    val fontFamilyId: String? = null,
    /** Optional per-resume text-size multiplier (FontSizeOption.scale); null means the layout's
     *  own default density (1.0x). */
    val fontSizeScale: Float? = null,
    val sectionOrder: String = DEFAULT_SECTION_ORDER,
    val hiddenSections: String = "",
    /** User-defined position in "My Resumes" (drag-to-reorder) — lower sorts first. All existing
     *  rows default to 0 on migration, so they keep their prior updatedAt-DESC order until the
     *  user actually drags something, at which point every visible row gets a distinct value. */
    val sortOrder: Int = 0,
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
