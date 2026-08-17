package co.resume.domain.engine

import co.resume.domain.theme.ColorRef

/**
 * The intermediate representation every renderer (PDF, DOCX, preview HTML) paints/emits from.
 * Everything is pre-measured and positioned in millimeters — no renderer is allowed to re-measure
 * text or decide page breaks; if a decision isn't already in this tree, no renderer may make it.
 */
data class SizeMm(val widthMm: Float, val heightMm: Float)
data class MarginsMm(val topMm: Float, val bottomMm: Float, val leftMm: Float, val rightMm: Float)
data class RectMm(val xMm: Float, val yMm: Float, val widthMm: Float, val heightMm: Float)

/** A4 page geometry — the one place these numbers exist; every renderer imports this. */
object PageGeometry {
    val A4_SIZE_MM = SizeMm(210f, 297f)
    val MARGINS_MM = MarginsMm(topMm = 20f, bottomMm = 20f, leftMm = 20f, rightMm = 20f)
}

enum class ClipShape { NONE, CIRCLE, ROUNDED_RECT }

data class TextStyleFlags(val bold: Boolean = false, val italic: Boolean = false, val underline: Boolean = false)

sealed class PositionedBlock {
    /** entryId tags the source entry (e.g. "workExperience:3") so exporters can group/bookmark without re-deriving it. */
    abstract val entryId: String?

    data class TextLine(
        val text: String,
        val xMm: Float,
        val yMm: Float,
        val widthMm: Float,
        val fontFamily: String,
        val fontAssetPath: String,
        val sizePt: Float,
        val color: ColorRef,
        val style: TextStyleFlags = TextStyleFlags(),
        override val entryId: String? = null,
    ) : PositionedBlock()

    data class Rule(
        val xMm: Float,
        val yMm: Float,
        val widthMm: Float,
        val strokePt: Float,
        val color: ColorRef,
        override val entryId: String? = null,
    ) : PositionedBlock()

    data class Rect(
        val boundsMm: RectMm,
        val fill: ColorRef?,
        val cornerRadiusMm: Float = 0f,
        override val entryId: String? = null,
    ) : PositionedBlock()

    data class Image(
        val boundsMm: RectMm,
        val bitmapPath: String,
        val clip: ClipShape = ClipShape.NONE,
        override val entryId: String? = null,
    ) : PositionedBlock()

    /** Decorative glyph (phone/email/location/skill-bar marker) drawn beside real text — never inside an image, never replacing text. */
    data class IconGlyph(
        val glyphKey: String,
        val xMm: Float,
        val yMm: Float,
        val sizeMm: Float,
        val color: ColorRef,
        override val entryId: String? = null,
    ) : PositionedBlock()
}

data class PageLayout(
    val pageNumber: Int,
    val blocks: List<PositionedBlock>,
    val footer: PositionedBlock.TextLine? = null,
)

data class ResumeLayout(
    val pages: List<PageLayout>,
    val pageSizeMm: SizeMm = PageGeometry.A4_SIZE_MM,
    val margins: MarginsMm = PageGeometry.MARGINS_MM,
)
