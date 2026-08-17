package co.resume.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * One message in the AI assistant popup's chat, scoped to whichever document the user had it open
 * from — exactly one of [resumeId]/[coverLetterId] is set. Kept in its own table (rather than as
 * some in-memory-only ViewModel state) so the conversation survives navigating away and back to
 * the same resume/cover letter within the app, and `onDelete = CASCADE` on both foreign keys means
 * deleting the resume/cover letter cleans up its chat automatically, matching "history sticks
 * around only as long as the document does."
 */
@Entity(
    tableName = "ai_chat_messages",
    foreignKeys = [
        ForeignKey(entity = ResumeEntity::class, parentColumns = ["id"], childColumns = ["resumeId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = CoverLetterEntity::class, parentColumns = ["id"], childColumns = ["coverLetterId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("resumeId"), Index("coverLetterId")]
)
data class AiChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val resumeId: Long? = null,
    val coverLetterId: Long? = null,
    /** "user" or "assistant", matching AiClient.ChatMessage's role strings. */
    val role: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)
