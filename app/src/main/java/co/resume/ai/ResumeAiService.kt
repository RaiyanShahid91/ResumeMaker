package co.resume.ai

object ResumeAiService {

    suspend fun generateObjective(designation: String, existingObjective: String = ""): String {
        val role = designation.ifBlank { "professional" }
        val prompt = if (existingObjective.isBlank()) {
            "Write a professional resume objective (2-3 sentences) for a $role. Be concise and results-oriented. Return only the text, no labels or quotes."
        } else {
            "Improve this resume objective for a $role. Use strong action words and make it more impactful. Return only the improved text, no labels or quotes:\n$existingObjective"
        }
        return AiClient.generate(prompt)
    }

    suspend fun improveJobDescription(jobTitle: String, company: String, description: String): String {
        val body = if (description.isBlank()) "Write relevant responsibilities for this role." else "Original:\n$description"
        val prompt = """
            Rewrite this as 2-3 concise bullet points for a $jobTitle at $company resume entry.
            Start each bullet with a strong action verb. Return only the bullet points, nothing else.
            $body
        """.trimIndent()
        return AiClient.generate(prompt)
    }

    suspend fun suggestSkills(designation: String): List<String> {
        val role = designation.ifBlank { "software developer" }
        val prompt = "List exactly 10 relevant skills for a $role resume. Return ONLY a comma-separated list of skill names, no numbering, no explanations."
        val result = AiClient.generate(prompt)
        return result.split(",").map { it.trim() }.filter { it.isNotBlank() }.take(10)
    }

    suspend fun generateProjectDescription(projectName: String, existingDescription: String = ""): String {
        val prompt = if (existingDescription.isBlank()) {
            "Write a concise 1-2 sentence resume project description for a project called \"$projectName\". Focus on what was built and the impact. Return only the description text."
        } else {
            "Improve this resume project description for a project called \"$projectName\". Make it more impactful and professional. Return only the improved text:\n$existingDescription"
        }
        return AiClient.generate(prompt)
    }

    suspend fun suggestAchievement(designation: String, existingAchievement: String = ""): String {
        val role = designation.ifBlank { "professional" }
        val prompt = if (existingAchievement.isBlank()) {
            "Write one strong, concise resume achievement bullet for a $role. Use metrics where possible. Return only the achievement text, no labels."
        } else {
            "Improve this resume achievement for a $role. Make it more impactful and quantified. Return only the improved text:\n$existingAchievement"
        }
        return AiClient.generate(prompt)
    }
}
