package co.resume.domain.importer

import org.json.JSONArray
import org.json.JSONObject

/**
 * Parses the AI's JSON-mode response (see `ResumeAiService.extractResumeData`) into
 * [ExtractedResumeData]. The model is asked to omit sections it didn't find, but "asked" isn't
 * "guaranteed" — every read here uses `opt*` with a blank/empty default rather than `get*`, so a
 * missing key never throws. [parse] only returns null when the top-level response itself isn't a
 * JSON object at all (e.g. the model wrapped it in prose despite JSON mode) — that's the one case
 * the caller should treat as a real failure rather than "this resume just didn't have much in it".
 */
object ResumeImportParser {

    fun parse(json: String): ExtractedResumeData? = runCatching {
        val root = JSONObject(json)
        ExtractedResumeData(
            name = root.optString("name"),
            email = root.optString("email"),
            phone = root.optString("phone"),
            address = root.optString("address"),
            objective = root.optString("objective"),
            workExperience = root.optJSONArray("workExperience").mapNotNullObjects { obj ->
                val jobTitle = obj.optString("jobTitle")
                val company = obj.optString("company")
                if (jobTitle.isBlank() && company.isBlank()) return@mapNotNullObjects null
                ExtractedWorkExperience(
                    jobTitle = jobTitle,
                    company = company,
                    durationFrom = obj.optString("durationFrom"),
                    durationTo = obj.optString("durationTo"),
                    description = obj.optString("description"),
                )
            },
            education = root.optJSONArray("education").mapNotNullObjects { obj ->
                val course = obj.optString("course")
                val university = obj.optString("university")
                if (course.isBlank() && university.isBlank()) return@mapNotNullObjects null
                ExtractedEducation(
                    course = course,
                    university = university,
                    grade = obj.optString("grade"),
                    durationFrom = obj.optString("durationFrom"),
                    durationTo = obj.optString("durationTo"),
                )
            },
            skills = root.optJSONArray("skills").mapNotNullStrings(),
            projects = root.optJSONArray("projects").mapNotNullObjects { obj ->
                val projectName = obj.optString("projectName")
                if (projectName.isBlank()) return@mapNotNullObjects null
                ExtractedProject(
                    projectName = projectName,
                    description = obj.optString("description"),
                    durationFrom = obj.optString("durationFrom"),
                    durationTo = obj.optString("durationTo"),
                    projectLink = obj.optString("projectLink"),
                )
            },
            achievements = root.optJSONArray("achievements").mapNotNullStrings(),
            languages = root.optJSONArray("languages").mapNotNullStrings(),
        )
    }.getOrNull()

    private inline fun <T> JSONArray?.mapNotNullObjects(transform: (JSONObject) -> T?): List<T> {
        if (this == null) return emptyList()
        return (0 until length()).mapNotNull { i -> optJSONObject(i)?.let(transform) }
    }

    private fun JSONArray?.mapNotNullStrings(): List<String> {
        if (this == null) return emptyList()
        return (0 until length()).mapNotNull { i -> optString(i).takeIf { it.isNotBlank() } }
    }
}
