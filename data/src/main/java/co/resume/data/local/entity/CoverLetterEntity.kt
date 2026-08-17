package co.resume.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cover_letters")
data class CoverLetterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val recipientName: String = "",
    val companyName: String = "",
    val jobTitle: String = "",
    val senderName: String = "",
    val senderEmail: String = "",
    val senderPhone: String = "",
    val date: String = "",
    val salutation: String = "",
    val bodyText: String = "",
    val closing: String = "",
    val templateId: Int = 1,
    val accentColorHex: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
