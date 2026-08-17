package co.resume.domain.layout

/** The resume sections the engine knows how to lay out — mirrors ResumeWithDetails' relations. */
enum class SectionType {
    OBJECTIVE, EDUCATION, WORK_EXPERIENCE, SKILLS, PROJECTS, ACHIEVEMENTS,
    LANGUAGES, INTERESTS, HOBBIES, DECLARATION,
}

enum class ColumnRole { MAIN, SIDEBAR }

data class ColumnSpec(
    val role: ColumnRole,
    val widthFraction: Float,
    val hasBackground: Boolean = false,
)

enum class HeaderStyle { TOP_BAR, PHOTO_BLOCK, COLOR_BLOCK, MINIMAL, TIMELINE_INLINE }

data class PhotoSlot(val shape: PhotoShape, val sizeMm: Float, val inHeader: Boolean)
enum class PhotoShape { CIRCLE, ROUNDED_SQUARE, SQUARE }

enum class HeadingStyle { UPPERCASE_UNDERLINE, ACCENT_BAR, BOLD_CAPS, SMALL_CAPS_DIVIDER }

/** Never allowed to replace real text or live inside an image — only ever drawn beside text. */
enum class IconPolicy { NONE, INLINE_WITH_TEXT }

data class DensityPreset(val baseFontSizePt: Float, val lineHeightMultiplier: Float, val sectionSpacingMm: Float)

/** ResumeEntity.fontSizeScale is an optional per-resume multiplier on top of the layout's own
 *  density preset — spacing scales along with the font size (not just the text itself) so a
 *  larger font still gets proportionally more breathing room instead of feeling cramped. */
fun DensityPreset.scaled(factor: Float): DensityPreset =
    if (factor == 1f) this else copy(baseFontSizePt = baseFontSizePt * factor, sectionSpacingMm = sectionSpacingMm * factor)

enum class FooterStyle { NONE, PAGE_NUMBER_CENTER, PAGE_NUMBER_RIGHT }

/**
 * A structural resume layout, expressed entirely as data consumed by the generic LayoutEngine —
 * not a subclass — so every layout shares one flow/pagination implementation instead of each
 * risking its own slightly-different behavior.
 */
data class LayoutDefinition(
    val id: String,
    val title: String,
    val columns: List<ColumnSpec>,
    val headerStyle: HeaderStyle,
    val photoSlot: PhotoSlot?,
    val sectionHeadingStyle: HeadingStyle,
    val iconPolicy: IconPolicy,
    val densityPreset: DensityPreset,
    val defaultSectionOrder: List<SectionType>,
    val sidebarSections: Set<SectionType> = emptySet(),
    val supportsPersonalitySections: Boolean = true,
    val footerStyle: FooterStyle = FooterStyle.NONE,
    val isAtsSafe: Boolean = false,
    /** Work/education entries get a marker dot + connecting rule down the left edge instead of a plain list. */
    val useTimelineMarkers: Boolean = false,
) {
    val mainColumn: ColumnSpec get() = columns.first { it.role == ColumnRole.MAIN }
    val sidebarColumn: ColumnSpec? get() = columns.firstOrNull { it.role == ColumnRole.SIDEBAR }
}
