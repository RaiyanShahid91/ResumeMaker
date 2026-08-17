package co.resume.ai

import co.resume.analytics.Analytics

object ResumeAiService {

    private suspend fun track(feature: String, block: suspend () -> String): String {
        Analytics.logEvent(Analytics.Event.AI_REQUESTED, mapOf(Analytics.Param.FEATURE to feature))
        val start = System.currentTimeMillis()
        return try {
            val result = block()
            Analytics.logEvent(
                Analytics.Event.AI_SUCCESS,
                mapOf(Analytics.Param.FEATURE to feature, Analytics.Param.LATENCY_MS to System.currentTimeMillis() - start)
            )
            result
        } catch (e: Exception) {
            Analytics.logEvent(
                Analytics.Event.AI_FAILED,
                mapOf(Analytics.Param.FEATURE to feature, Analytics.Param.ERROR_MESSAGE to e.message)
            )
            Analytics.logError("ResumeAiService.$feature", e)
            throw e
        }
    }

    suspend fun generateObjective(designation: String, existingObjective: String = ""): String =
        track("objective") {
            val role = designation.ifBlank { "professional" }
            val prompt = if (existingObjective.isBlank()) {
                "Write a professional resume objective (2-3 sentences) for a $role. Be concise and results-oriented. Return only the text, no labels or quotes."
            } else {
                "Improve this resume objective for a $role. Use strong action words and make it more impactful. Return only the improved text, no labels or quotes:\n$existingObjective"
            }
            AiClient.generate(prompt)
        }

    suspend fun improveJobDescription(jobTitle: String, company: String, description: String): String =
        track("job_description") {
            val instruction = if (description.isBlank()) {
                "Write 2-3 concise bullet points of relevant responsibilities and achievements for a $jobTitle at $company resume entry."
            } else {
                "Rewrite the following as 2-3 concise bullet points for a $jobTitle at $company resume entry:\n$description"
            }
            val prompt = """
                $instruction
                Start each bullet with a strong action verb. Return only the bullet points, nothing else.
            """.trimIndent()
            AiClient.generate(prompt)
        }

    suspend fun suggestSkills(designation: String): List<String> {
        val result = track("skills") {
            val role = designation.ifBlank { "software developer" }
            val prompt = "List exactly 10 relevant skills for a $role resume. Return ONLY a comma-separated list of skill names, no numbering, no explanations."
            AiClient.generate(prompt)
        }
        return result.split(",").map { it.trim() }.filter { it.isNotBlank() }.take(10)
    }

    suspend fun generateProjectDescription(projectName: String, existingDescription: String = ""): String =
        track("project_description") {
            val prompt = if (existingDescription.isBlank()) {
                "Write a concise 1-2 sentence resume project description for a project called \"$projectName\". Focus on what was built and the impact. Return only the description text."
            } else {
                "Improve this resume project description for a project called \"$projectName\". Make it more impactful and professional. Return only the improved text:\n$existingDescription"
            }
            AiClient.generate(prompt)
        }

    suspend fun suggestAchievement(designation: String, existingAchievement: String = ""): String =
        track("achievement") {
            val role = designation.ifBlank { "professional" }
            val prompt = if (existingAchievement.isBlank()) {
                "Write one strong, concise resume achievement bullet for a $role. Use metrics where possible. Return only the achievement text, no labels."
            } else {
                "Improve this resume achievement for a $role. Make it more impactful and quantified. Return only the improved text:\n$existingAchievement"
            }
            AiClient.generate(prompt)
        }

    suspend fun generateCoverLetter(
        designation: String,
        companyName: String,
        jobTitle: String,
        skills: List<String>,
        jobDescription: String = ""
    ): String = track("cover_letter") {
        val role = jobTitle.ifBlank { designation.ifBlank { "the role" } }
        val company = companyName.ifBlank { "the company" }
        val skillsLine = if (skills.isNotEmpty()) "Relevant skills: ${skills.joinToString(", ")}." else ""
        val jdLine = if (jobDescription.isNotBlank()) "Tailor it to this job description:\n$jobDescription" else ""
        val prompt = """
            Write a professional 3-paragraph cover letter body (no greeting, no closing signature)
            for a $designation applying for the $role position at $company.
            $skillsLine
            $jdLine
            Be concise, confident, and specific. Return only the body paragraphs, no labels or quotes.
        """.trimIndent()
        AiClient.generate(prompt)
    }

    suspend fun generateDeclaration(existingText: String = ""): String =
        track("declaration") {
            val prompt = if (existingText.isBlank()) {
                "Write a standard, professional resume declaration statement (1-2 sentences) affirming that the information provided is true to the best of the candidate's knowledge. Return only the declaration text, no labels or quotes."
            } else {
                "Improve this resume declaration statement, keeping it formal and concise. Return only the improved text:\n$existingText"
            }
            AiClient.generate(prompt)
        }

    /**
     * Structures raw resume text (extracted from an imported PDF/photo — see
     * `ResumeImportSource`/`ResumeImportParser`) into the JSON shape `ResumeImportParser` expects.
     * Explicitly instructed to omit anything not actually present rather than inventing
     * placeholder content — `ResumeImportParser` only ever persists non-empty sections, so an
     * over-eager model here would show up as fabricated resume content, not just a parsing bug.
     */
    suspend fun extractResumeData(rawText: String): String = track("resume_import") {
        val prompt = """
            Extract structured data from the resume text below and return ONLY a single JSON object
            (no markdown, no explanation, no code fences) with exactly this shape:
            {
              "name": string, "email": string, "phone": string, "address": string, "objective": string,
              "workExperience": [{"jobTitle": string, "company": string, "durationFrom": string, "durationTo": string, "description": string}],
              "education": [{"course": string, "university": string, "grade": string, "durationFrom": string, "durationTo": string}],
              "skills": [string],
              "projects": [{"projectName": string, "description": string, "durationFrom": string, "durationTo": string, "projectLink": string}],
              "achievements": [string],
              "languages": [string]
            }
            Rules:
            - Only include information that is actually present in the text below.
            - If a field or an entire section is not present, omit it or use "" / [] — never invent, guess, or use placeholder values.
            - "durationFrom"/"durationTo" should be short human-readable strings as written in the source (e.g. "Jan 2020", "Present"), not reformatted dates.
            - "description" for work experience should summarize responsibilities/achievements as written, not be copied verbatim if extremely long.

            Resume text:
            $rawText
        """.trimIndent()
        AiClient.generateJson(prompt)
    }
}
