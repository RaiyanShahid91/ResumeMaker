package co.resume.domain.export

data class ResumePalette(val name: String, val primary: String, val secondary: String)

object ResumePalettes {
    val all = listOf(
        ResumePalette("Blue",    "#1d4ed8", "#93c5fd"),
        ResumePalette("Indigo",  "#4338ca", "#a5b4fc"),
        ResumePalette("Purple",  "#7c3aed", "#c4b5fd"),
        ResumePalette("Rose",    "#e11d48", "#fda4af"),
        ResumePalette("Crimson", "#b91c1c", "#fca5a5"),
        ResumePalette("Orange",  "#ea580c", "#fdba74"),
        ResumePalette("Gold",    "#b45309", "#fcd34d"),
        ResumePalette("Teal",    "#0d9488", "#5eead4"),
        ResumePalette("Green",   "#16a34a", "#86efac"),
        ResumePalette("Cyan",    "#0891b2", "#67e8f9"),
        ResumePalette("Slate",   "#475569", "#94a3b8"),
        ResumePalette("Charcoal","#1e293b", "#64748b"),
    )

    fun fromHex(primaryHex: String): ResumePalette? = all.find { it.primary == primaryHex }
}
