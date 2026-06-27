package co.resume.domain.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import co.resumeai.R
import co.resume.data.local.entity.ResumeWithDetails
import java.io.ByteArrayOutputStream
import java.io.File

private val fontNames = arrayOf(
    "Arial", "Times", "Courier", "Consolas", "Cursive", "Abril Fatface", "Arvo", "Bad Script",
    "Bitter", "Comfortaa", "Courier Prime", "Dancing Script", "Darker Grotesque", "Dosis",
    "Fuzzy Bubbles", "Gentium Basic", "Inconsolata", "Indie Flower", "Joan", "Josefin Sans",
    "Libre Baskerville", "Libre Caslon Text", "Lobster", "Lora", "Montserrat", "Mulish", "Nunito",
    "Oooh Baby", "Open Sans", "Overpass", "Pacifico", "Playfair Display", "Poiret One", "Poppins",
    "Prompt", "Questrial", "Quicksand", "Raleway", "Roboto", "Rubik", "Shadows Into Light",
    "Stoke", "Ubuntu", "Varela Round"
)

private val colorCodes = arrayOf(
    "AliceBlue", "AntiqueWhite", "Aqua", "Aquamarine", "Azure", "Beige", "Bisque", "Black",
    "BlanchedAlmond", "Blue", "BlueViolet", "Brown", "BurlyWood", "CadetBlue", "Chartreuse",
    "Chocolate", "Coral", "CornflowerBlue", "Cornsilk", "Crimson", "Cyan", "DarkBlue", "DarkCyan",
    "DarkGoldenrod", "DarkGray", "DarkGreen", "DarkKhaki", "DarkMagenta", "DarkOliveGreen",
    "DarkOrange", "DarkOrchid", "DarkRed", "DarkSalmon", "DarkSeaGreen", "DarkSlateBlue",
    "DarkSlateGray", "DarkTurquoise", "DarkViolet", "DeepPink", "DeepSkyBlue", "DimGray",
    "DodgerBlue", "FireBrick", "FloralWhite", "ForestGreen", "Fuchsia", "Gainsboro", "GhostWhite",
    "Gold", "Goldenrod", "Gray", "Green", "GreenYellow", "HoneyDew", "HotPink", "IndianRed",
    "Indigo", "Ivory", "Khaki", "Lavender", "LavenderBlush", "LawnGreen", "LemonChiffon",
    "LightBlue", "LightCoral", "LightCyan", "LightGoldenrodYellow", "LightGray", "LightGreen",
    "LightPink", "LightSalmon", "LightSeaGreen", "LightSkyBlue", "LightSlateGray",
    "LightSteelBlue", "LightYellow", "Lime", "LimeGreen", "Linen", "Magenta", "Maroon",
    "MediumAquamarine", "MediumBlue", "MediumOrchid", "MediumPurple", "MediumSeaGreen",
    "MediumSlateBlue", "MediumSpringGreen", "MediumTurquoise", "MediumVioletRed", "MidnightBlue",
    "MintCream", "MistyRose", "Moccasin", "NavajoWhite", "Navy", "OldLace", "Olive", "OliveDrab",
    "Orange", "OrangeRed", "Orchid", "PaleGoldenrod", "PaleGreen", "PaleTurquoise",
    "PaleVioletRed", "PapayaWhip", "PeachPuff", "Peru", "Pink", "Plum", "PowderBlue", "Purple",
    "RebeccaPurple", "Red", "RosyBrown", "RoyalBlue", "SaddleBrown", "Salmon", "SandyBrown",
    "SeaGreen", "SeaShell", "Sienna", "Silver", "SkyBlue", "SlateBlue", "SlateGray", "Snow",
    "SpringGreen", "SteelBlue", "Tan", "Teal", "Thistle", "Tomato", "Turquoise", "Violet",
    "Wheat", "White", "WhiteSmoke", "Yellow", "YellowGreen"
)

/** Renders a [ResumeWithDetails] into the HTML contract expected by each template's `index.html`. */
object ResumeHtmlRenderer {

    fun render(context: Context, details: ResumeWithDetails): String {
        val resume = resume(details)
        val template = TemplateCatalog.resolve(resume.templateId)
        val templateHtml = context.assets.open("templates/${template.assetFolder}/index.html")
            .bufferedReader().use { it.readText() }

        val html = StringBuilder()
        html.append(p("sample_resume_name", resume.name))
        html.append(p("sample_resume_email", resume.email))
        html.append(p("sample_resume_phone", resume.phone))
        html.append(p("sample_resume_address", nl2br(resume.address)))
        html.append(p("sample_objective", nl2br(resume.objective)))

        html.append(section("sample_educational_details", "element_educational_details", details.education) {
            p("template_education_course", it.course) +
                p("template_education_university", it.university) +
                p("template_education_grade", "Grade: <b>${it.grade}</b>") +
                p("template_education_duration", "${it.durationFrom} - ${it.durationTo}")
        })

        html.append(section("sample_work_experience", "element_work_experience", details.workExperience) {
            p("template_experience_job", it.jobTitle) +
                p("template_experience_company", it.company) +
                p("template_experience_duration", "${it.durationFrom} - ${it.durationTo}") +
                p("template_experience_description", nl2br(it.description))
        })

        html.append(section("sample_projects_details", "element_projects_details", details.projects) {
            p("template_project_name", it.projectName) +
                p("template_project_description", nl2br(it.description)) +
                p("template_project_duration", "${it.durationFrom} - ${it.durationTo}") +
                p("template_project_link", it.projectLink)
        })

        html.append(section("sample_achievements_details", "element_achievements_details", details.achievements) {
            p("template_achievement_name", it.achievementName)
        })

        html.append(section("sample_skills_details", "element_skills_details", details.skills) {
            p("template_skill_name", it.skillName) + p("template_skill_level", it.skillLevel)
        })

        html.append(section("sample_interests_details", "element_interests_details", details.interests) {
            p("template_interest_name", it.interestName)
        })

        html.append(section("sample_hobbies_details", "element_hobbies_details", details.hobbies) {
            p("template_hobby_name", it.hobbyName)
        })

        html.append(section("sample_languages_details", "element_languages_details", details.languages) {
            p("template_language_name", it.languageName)
        })

        html.append(p("sample_declaration", nl2br(resume.declaration)))
        html.append(p("sample_declaration_place", "Place: <b>${nl2br(resume.declarationPlace)}</b>"))
        html.append(p("sample_declaration_date", "Date: <b>${nl2br(resume.declarationDate)}</b>"))

        if (details.workExperience.isEmpty()) html.append(hideStyle("div_work_experience"))
        if (details.education.isEmpty()) html.append(hideStyle("div_educational_details"))
        if (details.projects.isEmpty()) html.append(hideStyle("div_projects_details"))
        if (details.achievements.isEmpty()) html.append(hideStyle("div_achievements_details"))
        if (details.skills.isEmpty()) html.append(hideStyle("div_skills_details"))
        if (details.interests.isEmpty()) html.append(hideStyle("div_interests_details"))
        if (details.hobbies.isEmpty()) html.append(hideStyle("div_hobbies_details"))
        if (details.languages.isEmpty()) html.append(hideStyle("div_languages_details"))

        html.append(imageDiv("sample_resume_image", "profile-img", context, resume.profilePhotoPath, R.drawable.icon_resume_profile_photo))
        html.append(imageDiv("sample_resume_signature", "signature-img", context, resume.signaturePath, null))

        html.append(templateHtml)

        val fontName = fontNames.getOrElse(resume.fontFamilyIndex) { fontNames[0] }
        html.append(
            "<style>@import url('https://fonts.googleapis.com/css2?family=$fontName&display=swap');\n" +
                "button, html, select, input{font-family:'$fontName';}</style>"
        )

        val accentHex = resume.accentColorHex
        val palette = if (accentHex != null) ResumePalettes.fromHex(accentHex) else null
        val colorOne = palette?.primary ?: accentHex ?: colorCodes.getOrElse(resume.colorOneIndex) { colorCodes[41] }
        val colorTwo = palette?.secondary ?: colorCodes.getOrElse(resume.colorTwoIndex) { colorCodes[39] }
        html.append("<style>:root{--color-one: $colorOne;--color-two: $colorTwo;}</style>")

        return html.toString()
    }

    private fun resume(details: ResumeWithDetails) = details.resume

    private fun p(cls: String, value: String) = "<p class='$cls'>$value</p>"

    private fun nl2br(value: String) = value.replace("\n", "<br>")

    private inline fun <T> section(
        outerClass: String,
        elementClass: String,
        items: List<T>,
        crossinline renderItem: (T) -> String
    ): String {
        val body = items.joinToString(separator = "") { "<div class='$elementClass'>${renderItem(it)}</div>" }
        return "<div class='$outerClass'>$body</div>"
    }

    private fun hideStyle(cls: String) = "<style>.$cls{display:none;}</style>"

    private fun imageDiv(
        outerClass: String,
        imgClass: String,
        context: Context,
        path: String?,
        fallbackDrawable: Int?
    ): String {
        val base64 = encodeImage(context, path, fallbackDrawable) ?: return "<div class='$outerClass'></div>"
        return "<div class='$outerClass'><img class='$imgClass' src='data:image/png;base64,$base64' /></div>"
    }

    private fun encodeImage(context: Context, path: String?, fallbackDrawable: Int?): String? {
        val bitmap: Bitmap? = when {
            !path.isNullOrBlank() && File(path).exists() -> BitmapFactory.decodeFile(path)
            fallbackDrawable != null -> BitmapFactory.decodeResource(context.resources, fallbackDrawable)
            else -> null
        }
        bitmap ?: return null
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
    }
}
