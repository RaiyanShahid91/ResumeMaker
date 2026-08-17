package co.resume.domain.theme

/** One selectable font family, applied uniformly as both the heading and body face — simpler
 *  for a user-facing picker than choosing heading/body independently, which is what
 *  [ThemeCatalog]'s curated pairings already do per-theme. Only families with a genuine
 *  regular+bold pair bundled under templates/src/main/assets/fonts/ are listed here (Quicksand
 *  and Roboto only have one weight each on disk today, so they stay theme-only pairings rather
 *  than becoming independently selectable). */
data class FontOption(val id: String, val displayName: String, val pairing: FontPairing)

object FontCatalog {
    val all: List<FontOption> = listOf(
        FontOption(
            id = "inter",
            displayName = "Inter",
            pairing = FontPairing(
                heading = FontRef("Inter", "fonts/inter_medium.ttf", FontWeight.MEDIUM),
                body = FontRef("Inter", "fonts/inter_regular.ttf", FontWeight.REGULAR),
            ),
        ),
        FontOption(
            id = "fira_sans",
            displayName = "Fira Sans",
            pairing = FontPairing(
                heading = FontRef("Fira Sans", "fonts/fira_sans_bold.ttf", FontWeight.BOLD),
                body = FontRef("Fira Sans", "fonts/fira_sans_regular.ttf", FontWeight.REGULAR),
            ),
        ),
        FontOption(
            id = "pt_sans",
            displayName = "PT Sans",
            pairing = FontPairing(
                heading = FontRef("PT Sans", "fonts/pt_sans_bold.ttf", FontWeight.BOLD),
                body = FontRef("PT Sans", "fonts/pt_sans_regular.ttf", FontWeight.REGULAR),
            ),
        ),
        FontOption(
            id = "poppins",
            displayName = "Poppins",
            pairing = FontPairing(
                heading = FontRef("Poppins", "fonts/poppins_bold.ttf", FontWeight.BOLD),
                body = FontRef("Poppins", "fonts/poppins_regular.ttf", FontWeight.REGULAR),
            ),
        ),
        FontOption(
            id = "pt_serif",
            displayName = "PT Serif",
            pairing = FontPairing(
                heading = FontRef("PT Serif", "fonts/pt_serif_bold.ttf", FontWeight.BOLD),
                body = FontRef("PT Serif", "fonts/pt_serif_regular.ttf", FontWeight.REGULAR),
            ),
        ),
        FontOption(
            id = "crimson_text",
            displayName = "Crimson Text",
            pairing = FontPairing(
                heading = FontRef("Crimson Text", "fonts/crimson_text_bold.ttf", FontWeight.BOLD),
                body = FontRef("Crimson Text", "fonts/crimson_text_regular.ttf", FontWeight.REGULAR),
            ),
        ),
        FontOption(
            id = "spectral",
            displayName = "Spectral",
            pairing = FontPairing(
                heading = FontRef("Spectral", "fonts/spectral_bold.ttf", FontWeight.BOLD),
                body = FontRef("Spectral", "fonts/spectral_regular.ttf", FontWeight.REGULAR),
            ),
        ),
    )

    /** Null (rather than falling back to a default) when [fontFamilyId] is null/unknown — that's
     *  the signal callers use to mean "keep the theme's own font pairing", same as
     *  [ThemeCatalog.resolve] does the opposite (always returns something, since a theme is never optional). */
    fun resolve(fontFamilyId: String?): FontOption? = all.firstOrNull { it.id == fontFamilyId }
}

/** A small fixed set of relative sizes rather than a free-form input — every size in
 *  [LayoutEngine][co.resume.domain.engine.LayoutEngine] is tuned in points against the layout's
 *  own density preset, so letting the multiplier drift far from 1.0 risks overflow/pagination
 *  glitches no layout was designed to handle. */
enum class FontSizeOption(val id: String, val displayName: String, val scale: Float) {
    SMALL("small", "Small", 0.92f),
    STANDARD("standard", "Standard", 1f),
    LARGE("large", "Large", 1.08f);

    companion object {
        fun resolve(id: String?): FontSizeOption = entries.firstOrNull { it.id == id } ?: STANDARD
    }
}
