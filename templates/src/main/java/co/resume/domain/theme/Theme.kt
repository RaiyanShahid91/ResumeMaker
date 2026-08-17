package co.resume.domain.theme

enum class FontWeight { REGULAR, MEDIUM, BOLD }

/** A font asset bundled under templates/src/main/assets/fonts/. */
data class FontRef(val family: String, val assetPath: String, val weight: FontWeight = FontWeight.REGULAR)

data class FontPairing(val heading: FontRef, val body: FontRef)

data class ColorRef(val hex: String) {
    init { require(hex.matches(Regex("^#[0-9a-fA-F]{6}$"))) { "ColorRef expects #rrggbb, got $hex" } }
}

enum class BackgroundStyle { NONE, TINT, SOLID_BLOCK }

/**
 * Color + font, curated as a pair rather than chosen independently — replaces the old
 * ResumePalette/colorCodes-array/fontFamilyIndex scheme, where color and font were combined in
 * ways never explicitly designed for.
 */
data class Theme(
    val id: String,
    val name: String,
    val accentColor: ColorRef,
    val secondaryColor: ColorRef,
    val backgroundStyle: BackgroundStyle,
    val fontPairing: FontPairing,
)

/** ResumeEntity.accentColorHex is an optional per-resume override on top of the theme's own accent. */
fun Theme.withAccentOverride(hex: String?): Theme =
    if (hex.isNullOrBlank()) this else copy(accentColor = ColorRef(hex))

/** ResumeEntity.fontFamilyId is an optional per-resume override on top of the theme's own font
 *  pairing — same "null means use the theme's own value" pattern as [withAccentOverride]. */
fun Theme.withFontOverride(pairing: FontPairing?): Theme =
    if (pairing == null) this else copy(fontPairing = pairing)
