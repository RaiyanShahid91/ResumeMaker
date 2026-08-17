package co.resume.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "achievements",
    foreignKeys = [ForeignKey(
        entity = ResumeEntity::class,
        parentColumns = ["id"],
        childColumns = ["resumeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("resumeId")]
)
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val resumeId: Long,
    val orderIndex: Int,
    val achievementName: String = ""
)
