package co.resume.domain.importer

/**
 * What the AI managed to pull out of an imported resume PDF/photo. Deliberately not the Room
 * entities (`WorkExperienceEntity` etc.) — those carry `resumeId`/`orderIndex` that only exist
 * once a resume row has been created, and coupling extraction to the DB shape would make
 * [ResumeImportParser] harder to test in isolation. Every field/list defaults to blank/empty
 * rather than null-checked individually at every call site — a resume section the source
 * document didn't have (or the model couldn't find) is simply absent, not an error.
 */
data class ExtractedResumeData(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val objective: String = "",
    val workExperience: List<ExtractedWorkExperience> = emptyList(),
    val education: List<ExtractedEducation> = emptyList(),
    val skills: List<String> = emptyList(),
    val projects: List<ExtractedProject> = emptyList(),
    val achievements: List<String> = emptyList(),
    val languages: List<String> = emptyList(),
)

data class ExtractedWorkExperience(
    val jobTitle: String,
    val company: String,
    val durationFrom: String,
    val durationTo: String,
    val description: String,
)

data class ExtractedEducation(
    val course: String,
    val university: String,
    val grade: String,
    val durationFrom: String,
    val durationTo: String,
)

data class ExtractedProject(
    val projectName: String,
    val description: String,
    val durationFrom: String,
    val durationTo: String,
    val projectLink: String,
)
