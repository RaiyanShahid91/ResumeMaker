package co.resume.domain.theme

// Starter font set reused from the app's existing licensed UI fonts (templates/src/main/assets/fonts/) —
// pairing variety today comes from color, not typeface; a wider curated font-pairing set is a follow-up
// once more TTFs are sourced (theming is decoupled from layout precisely so that's a drop-in later change).
private val bodyRegular = FontRef("Inter", "fonts/inter_regular.ttf", FontWeight.REGULAR)
private val bodyMedium = FontRef("Inter", "fonts/inter_medium.ttf", FontWeight.MEDIUM)
private val headingBold = FontRef("Roboto", "fonts/roboto_bold.ttf", FontWeight.BOLD)
private val headingRounded = FontRef("Quicksand", "fonts/quicksand_regular.ttf", FontWeight.REGULAR)

val SlateClassicTheme = Theme(
    id = "slate_classic", name = "Slate Classic",
    accentColor = ColorRef("#1F2937"), secondaryColor = ColorRef("#4B5563"),
    backgroundStyle = BackgroundStyle.NONE,
    fontPairing = FontPairing(heading = headingBold, body = bodyRegular),
)

val SapphireTheme = Theme(
    id = "sapphire", name = "Sapphire",
    accentColor = ColorRef("#1D4ED8"), secondaryColor = ColorRef("#1E293B"),
    backgroundStyle = BackgroundStyle.TINT,
    fontPairing = FontPairing(heading = headingBold, body = bodyMedium),
)

val EmeraldTheme = Theme(
    id = "emerald", name = "Emerald",
    accentColor = ColorRef("#047857"), secondaryColor = ColorRef("#374151"),
    backgroundStyle = BackgroundStyle.NONE,
    fontPairing = FontPairing(heading = headingRounded, body = bodyRegular),
)

val CrimsonTheme = Theme(
    id = "crimson", name = "Crimson",
    accentColor = ColorRef("#B91C1C"), secondaryColor = ColorRef("#374151"),
    backgroundStyle = BackgroundStyle.TINT,
    fontPairing = FontPairing(heading = headingBold, body = bodyRegular),
)

val CharcoalGoldTheme = Theme(
    id = "charcoal_gold", name = "Charcoal & Gold",
    accentColor = ColorRef("#92400E"), secondaryColor = ColorRef("#1F2937"),
    backgroundStyle = BackgroundStyle.SOLID_BLOCK,
    fontPairing = FontPairing(heading = headingBold, body = bodyMedium),
)

val PlumTheme = Theme(
    id = "plum", name = "Plum",
    accentColor = ColorRef("#6D28D9"), secondaryColor = ColorRef("#374151"),
    backgroundStyle = BackgroundStyle.NONE,
    fontPairing = FontPairing(heading = headingRounded, body = bodyRegular),
)

val TealBreezeTheme = Theme(
    id = "teal_breeze", name = "Teal Breeze",
    accentColor = ColorRef("#0F766E"), secondaryColor = ColorRef("#1F2937"),
    backgroundStyle = BackgroundStyle.TINT,
    fontPairing = FontPairing(heading = headingBold, body = bodyMedium),
)

val MedicalBlueTheme = Theme(
    id = "medical_blue", name = "Medical Blue",
    accentColor = ColorRef("#0369A1"), secondaryColor = ColorRef("#334155"),
    backgroundStyle = BackgroundStyle.TINT,
    fontPairing = FontPairing(heading = headingBold, body = bodyRegular),
)

val LegalNavyTheme = Theme(
    id = "legal_navy", name = "Legal Navy",
    accentColor = ColorRef("#1E3A5F"), secondaryColor = ColorRef("#52525B"),
    backgroundStyle = BackgroundStyle.NONE,
    fontPairing = FontPairing(heading = headingBold, body = bodyRegular),
)

val FinanceForestTheme = Theme(
    id = "finance_forest", name = "Finance Forest",
    accentColor = ColorRef("#14532D"), secondaryColor = ColorRef("#1F2937"),
    backgroundStyle = BackgroundStyle.SOLID_BLOCK,
    fontPairing = FontPairing(heading = headingBold, body = bodyMedium),
)

val CreativeCoralTheme = Theme(
    id = "creative_coral", name = "Creative Coral",
    accentColor = ColorRef("#DB2777"), secondaryColor = ColorRef("#44403C"),
    backgroundStyle = BackgroundStyle.TINT,
    fontPairing = FontPairing(heading = headingRounded, body = bodyMedium),
)

val TechIndigoTheme = Theme(
    id = "tech_indigo", name = "Tech Indigo",
    accentColor = ColorRef("#4338CA"), secondaryColor = ColorRef("#1E293B"),
    backgroundStyle = BackgroundStyle.SOLID_BLOCK,
    fontPairing = FontPairing(heading = headingBold, body = bodyMedium),
)

val NonprofitAmberTheme = Theme(
    id = "nonprofit_amber", name = "Nonprofit Amber",
    accentColor = ColorRef("#B45309"), secondaryColor = ColorRef("#44403C"),
    backgroundStyle = BackgroundStyle.NONE,
    fontPairing = FontPairing(heading = headingRounded, body = bodyRegular),
)

val RealEstateClayTheme = Theme(
    id = "real_estate_clay", name = "Real Estate Clay",
    accentColor = ColorRef("#9A3412"), secondaryColor = ColorRef("#57534E"),
    backgroundStyle = BackgroundStyle.TINT,
    fontPairing = FontPairing(heading = headingBold, body = bodyRegular),
)

val AcademicBurgundyTheme = Theme(
    id = "academic_burgundy", name = "Academic Burgundy",
    accentColor = ColorRef("#7F1D1D"), secondaryColor = ColorRef("#3F3F46"),
    backgroundStyle = BackgroundStyle.NONE,
    fontPairing = FontPairing(heading = headingBold, body = bodyRegular),
)

val StartupVioletTheme = Theme(
    id = "startup_violet", name = "Startup Violet",
    accentColor = ColorRef("#7C3AED"), secondaryColor = ColorRef("#27272A"),
    backgroundStyle = BackgroundStyle.SOLID_BLOCK,
    fontPairing = FontPairing(heading = headingRounded, body = bodyMedium),
)

val HospitalityRoseTheme = Theme(
    id = "hospitality_rose", name = "Hospitality Rose",
    accentColor = ColorRef("#BE185D"), secondaryColor = ColorRef("#57534E"),
    backgroundStyle = BackgroundStyle.TINT,
    fontPairing = FontPairing(heading = headingRounded, body = bodyRegular),
)

object ThemeCatalog {
    val all: List<Theme> = listOf(
        SlateClassicTheme, SapphireTheme, EmeraldTheme, CrimsonTheme,
        CharcoalGoldTheme, PlumTheme, TealBreezeTheme,
        MedicalBlueTheme, LegalNavyTheme, FinanceForestTheme, CreativeCoralTheme,
        TechIndigoTheme, NonprofitAmberTheme, RealEstateClayTheme, AcademicBurgundyTheme,
        StartupVioletTheme, HospitalityRoseTheme,
    )

    fun resolve(themeId: String): Theme = all.firstOrNull { it.id == themeId } ?: SlateClassicTheme
}
