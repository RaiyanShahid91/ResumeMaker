package co.resume.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val DEFAULT_SECTION_ORDER =
    "objective,education,work_experience,skills,projects,achievements,languages,interests,hobbies,declaration"

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
    val templateId: Int = 1,
    val fontFamilyIndex: Int = 0,
    val colorOneIndex: Int = 41,
    val colorTwoIndex: Int = 39,
    val accentColorHex: String? = null,
    val sectionOrder: String = DEFAULT_SECTION_ORDER,
    val hiddenSections: String = "",
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
