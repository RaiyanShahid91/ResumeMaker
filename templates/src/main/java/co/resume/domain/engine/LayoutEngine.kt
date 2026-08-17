package co.resume.domain.engine

import co.resume.data.local.entity.ResumeWithDetails
import co.resume.domain.export.RichBlock
import co.resume.domain.export.RichSpan
import co.resume.domain.layout.ColumnRole
import co.resume.domain.layout.FooterStyle
import co.resume.domain.layout.HeaderStyle
import co.resume.domain.layout.HeadingStyle
import co.resume.domain.layout.LayoutDefinition
import co.resume.domain.layout.PhotoShape
import co.resume.domain.layout.scaled
import co.resume.domain.theme.ColorRef
import co.resume.domain.theme.FontRef
import co.resume.domain.theme.Theme
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

private const val BODY_GAP_MM = 1.2f
private const val ENTRY_GAP_MM = 3f
private const val BULLET_INDENT_MM = 4f
private const val TIMELINE_INDENT_MM = 5f
private const val ICON_DOT_MM = 1.6f
private const val COLUMN_GAP_MM = 6f
private const val HEADER_RECT_PADDING_MM = 6f

/**
 * Builds the canonical ResumeLayout IR from resume data + a layout + a theme. The single place
 * flow/pagination/entry-atomicity decisions are made — every renderer (PDF, DOCX, preview HTML)
 * only paints/emits what this produces, never re-measures.
 *
 * Multi-column layouts flow their main and sidebar columns independently against the same page
 * grid (each producing its own per-page block list) and are merged page-by-page, so a sidebar can
 * run out before the main column or continue onto a trailing page after it — both are real,
 * deliberately supported states, not accidents.
 */
@Singleton
class LayoutEngine @Inject constructor(private val textMeasurer: TextMeasurer) {

    fun build(resume: ResumeWithDetails, layout: LayoutDefinition, theme: Theme, sizeScale: Float = 1f): ResumeLayout {
        val margins = PageGeometry.MARGINS_MM
        val pageSize = PageGeometry.A4_SIZE_MM
        val contentWidthMm = pageSize.widthMm - margins.leftMm - margins.rightMm
        val density = layout.densityPreset.scaled(sizeScale)
        val bodyFont = theme.fontPairing.body
        val headingFont = theme.fontPairing.heading

        val sidebarSpec = layout.sidebarColumn
        val mainWidthMm = contentWidthMm * layout.mainColumn.widthFraction - (if (sidebarSpec != null) COLUMN_GAP_MM / 2 else 0f)
        val sidebarWidthMm = sidebarSpec?.let { contentWidthMm * it.widthFraction - COLUMN_GAP_MM / 2 } ?: 0f
        val sidebarIsFirstColumn = layout.columns.firstOrNull()?.role == ColumnRole.SIDEBAR
        val sidebarXMm = if (sidebarIsFirstColumn) margins.leftMm else margins.leftMm + mainWidthMm + COLUMN_GAP_MM
        val mainXMm = if (sidebarIsFirstColumn) margins.leftMm + sidebarWidthMm + COLUMN_GAP_MM else margins.leftMm

        // A sidebar with its own tint is a self-contained "identity card" in most resume designs —
        // splitting name/contact into the full-width header (white/color-block background) while
        // education/skills sit in the tinted sidebar right below made the personal info look like it
        // belonged to a different section, with an inconsistent background right at the seam. When
        // the sidebar has a background, the identity block (photo + name + designation + contact)
        // moves into the sidebar itself instead, so it reads as one continuous card; the header is
        // then skipped entirely and both columns start flush at the top margin.
        val sidebarHasBackground = sidebarSpec?.hasBackground == true

        val (headerBlocks, headerHeight) = if (sidebarHasBackground) {
            emptyList<PositionedBlock>() to 0f
        } else {
            buildHeader(resume, layout, margins.leftMm, margins.topMm, contentWidthMm, headingFont, bodyFont, theme.accentColor, sizeScale)
        }
        // COLOR_BLOCK headers paint a decorative band HEADER_RECT_PADDING_MM below the header text
        // itself (see the Rect below) purely for visual breathing room inside the color block —
        // content must start after THAT band ends, not merely after the header text, or the first
        // section heading gets drawn underneath the tail of the band. sectionSpacingMm on top of
        // that keeps a real gap between the band and the first section, per design (previously the
        // gap was density.sectionSpacingMm alone, which was smaller than the band's own padding,
        // so the section heading was actually drawn 1.5mm inside the band).
        val headerBandPaddingMm = if (!sidebarHasBackground && layout.headerStyle == HeaderStyle.COLOR_BLOCK) HEADER_RECT_PADDING_MM else 0f
        val headerBottomMm = if (sidebarHasBackground) margins.topMm else margins.topMm + headerHeight + headerBandPaddingMm + density.sectionSpacingMm

        val allSections = SectionContentBuilder.build(resume)
        val sidebarSections = allSections.filter { sidebarSpec != null && it.type in layout.sidebarSections }
        val mainSections = allSections.filter { it !in sidebarSections }

        var sidebarTopBlocks = emptyList<PositionedBlock>()
        var sidebarTopHeight = 0f
        if (sidebarHasBackground) {
            val (blocks, height) = buildSidebarIdentity(resume, layout, sidebarXMm, headerBottomMm, sidebarWidthMm, headingFont, bodyFont, theme.accentColor, sizeScale)
            sidebarTopBlocks = blocks
            sidebarTopHeight = height + density.sectionSpacingMm
        } else if (layout.photoSlot != null && layout.photoSlot.inHeader.not() && sidebarSpec != null) {
            val (blocks, height) = photoBlock(resume, layout.photoSlot, sidebarXMm, headerBottomMm, sidebarWidthMm)
            sidebarTopBlocks = blocks
            sidebarTopHeight = height + density.sectionSpacingMm
        }

        val mainPages = flowColumn(mainSections, mainXMm, mainWidthMm, headerBottomMm, margins, pageSize, density, bodyFont, theme, layout)
        val sidebarPages = if (sidebarSpec != null) {
            flowColumn(sidebarSections, sidebarXMm, sidebarWidthMm, headerBottomMm + sidebarTopHeight, margins, pageSize, density, bodyFont, theme, layout)
        } else emptyList()

        val totalPages = max(1, max(mainPages.size, sidebarPages.size))
        val pageLayouts = (0 until totalPages).map { i ->
            val blocks = mutableListOf<PositionedBlock>()
            if (sidebarSpec != null && sidebarSpec.hasBackground) {
                blocks += PositionedBlock.Rect(RectMm(sidebarXMm - 4f, 0f, sidebarWidthMm + 8f, pageSize.heightMm), tint(theme.accentColor))
            }
            if (i == 0) {
                if (!sidebarHasBackground && layout.headerStyle == HeaderStyle.COLOR_BLOCK) {
                    blocks += PositionedBlock.Rect(RectMm(0f, 0f, pageSize.widthMm, margins.topMm + headerHeight + HEADER_RECT_PADDING_MM), theme.accentColor)
                }
                blocks += headerBlocks
                blocks += sidebarTopBlocks
            }
            mainPages.getOrNull(i)?.let { blocks += it }
            sidebarPages.getOrNull(i)?.let { blocks += it }
            val footer = footerFor(layout, i + 1, totalPages, pageSize, margins, bodyFont, theme.secondaryColor, sizeScale)
            PageLayout(pageNumber = i + 1, blocks = blocks, footer = footer)
        }
        return ResumeLayout(pages = pageLayouts, pageSizeMm = pageSize, margins = margins)
    }

    /** Photo + name + designation + contact fields, stacked as one column inside the sidebar itself
     * — used instead of [buildHeader] whenever the sidebar has its own tinted background, so the
     * whole identity block sits on one consistent background instead of splitting across the
     * full-width header and the tinted sidebar underneath it. */
    private fun buildSidebarIdentity(
        resume: ResumeWithDetails, layout: LayoutDefinition, xMm: Float, yMm: Float, widthMm: Float,
        headingFont: FontRef, bodyFont: FontRef, accent: ColorRef, sizeScale: Float = 1f,
    ): Pair<List<PositionedBlock>, Float> {
        val nameSizePt = 15f * sizeScale
        val designationSizePt = 10.5f * sizeScale
        val contactSizePt = 9f * sizeScale
        val blocks = mutableListOf<PositionedBlock>()
        var y = yMm

        layout.photoSlot?.let { slot ->
            val (photoBlocks, photoHeight) = photoBlock(resume, slot, xMm, y, widthMm)
            if (photoBlocks.isNotEmpty()) {
                blocks += photoBlocks
                y += photoHeight + BODY_GAP_MM * 2
            }
        }

        val nameLineHeight = textMeasurer.lineHeightMm(nameSizePt, 1.2f)
        for (line in textMeasurer.wrapToWidth(resume.resume.name, headingFont, nameSizePt, widthMm)) {
            blocks += PositionedBlock.TextLine(line, xMm, y, widthMm, headingFont.family, headingFont.assetPath, nameSizePt, accent, TextStyleFlags(bold = true))
            y += nameLineHeight
        }
        if (resume.resume.designation.isNotBlank()) {
            val designationLineHeight = textMeasurer.lineHeightMm(designationSizePt, 1.2f)
            for (line in textMeasurer.wrapToWidth(resume.resume.designation, bodyFont, designationSizePt, widthMm)) {
                blocks += PositionedBlock.TextLine(line, xMm, y, widthMm, bodyFont.family, bodyFont.assetPath, designationSizePt, accent)
                y += designationLineHeight
            }
        }

        val contactFields = listOf(resume.resume.email, resume.resume.phone, resume.resume.address).filter { it.isNotBlank() }
        if (contactFields.isNotEmpty()) {
            y += BODY_GAP_MM
            val contactLineHeight = textMeasurer.lineHeightMm(contactSizePt, 1.2f)
            val prefixWidth = ICON_DOT_MM + 1.5f
            // One field per line (rather than the header's pack-them-inline-with-bullets treatment)
            // — the sidebar is narrow enough that packing email/phone/address onto shared lines just
            // reproduces the tight-wrap collisions the column-overlap fix above already had to guard
            // against, for no real space savings.
            for (field in contactFields) {
                for ((lineIndex, lineText) in textMeasurer.wrapToWidth(field, bodyFont, contactSizePt, widthMm - prefixWidth).withIndex()) {
                    if (lineIndex == 0) {
                        blocks += PositionedBlock.IconGlyph("dot", xMm, y + contactSizePt * 0.32f, ICON_DOT_MM, accent)
                    }
                    blocks += PositionedBlock.TextLine(lineText, xMm + prefixWidth, y, widthMm - prefixWidth, bodyFont.family, bodyFont.assetPath, contactSizePt, accent)
                    y += contactLineHeight
                }
            }
        }
        return blocks to (y - yMm)
    }

    /** Flows a set of sections down one column, independently paginating it; returns one block list per page it occupies. */
    private fun flowColumn(
        sections: List<ResumeSection>, xMm: Float, widthMm: Float, startYFirstPage: Float,
        margins: MarginsMm, pageSize: SizeMm, density: co.resume.domain.layout.DensityPreset,
        bodyFont: FontRef, theme: Theme, layout: LayoutDefinition,
    ): List<List<PositionedBlock>> {
        if (sections.isEmpty()) return emptyList()
        val headingFont = theme.fontPairing.heading
        val pages = mutableListOf<MutableList<PositionedBlock>>(mutableListOf())
        var pageIndex = 0
        var cursorYMm = startYFirstPage
        fun remaining() = pageSize.heightMm - margins.bottomMm - cursorYMm
        fun newPage() {
            pages.add(mutableListOf())
            pageIndex++
            cursorYMm = margins.topMm
        }
        fun place(blocks: List<PositionedBlock>, heightMm: Float) {
            pages[pageIndex].addAll(blocks)
            cursorYMm += heightMm
        }

        for (section in sections) {
            val headingHeightMm = textMeasurer.lineHeightMm(density.baseFontSizePt + 1.5f, density.lineHeightMultiplier)
            val timeline = layout.useTimelineMarkers && section.type in TIMELINE_SECTION_TYPES
            val firstEntry = section.entries.first()
            val (firstBlocksRel, firstHeightMm) = measureEntry(firstEntry, widthMm, density.baseFontSizePt, density.lineHeightMultiplier, bodyFont, theme.secondaryColor, theme.accentColor, timeline)

            if (remaining() < headingHeightMm + firstHeightMm && cursorYMm > margins.topMm) newPage()

            val headingBlocks = headingBlocks(section.heading, xMm, cursorYMm, widthMm, density.baseFontSizePt + 1.5f, headingFont, theme.accentColor, layout.sectionHeadingStyle)
            place(headingBlocks.map { offsetBlock(it, 0f, 0f) }, headingHeightMm + 1.5f)

            for ((index, entry) in section.entries.withIndex()) {
                val (blocksRel, heightMm) = if (index == 0) firstBlocksRel to firstHeightMm else
                    measureEntry(entry, widthMm, density.baseFontSizePt, density.lineHeightMultiplier, bodyFont, theme.secondaryColor, theme.accentColor, timeline)

                val fitsCurrentPage = heightMm <= remaining()
                val fitsFreshPage = heightMm <= pageSize.heightMm - margins.topMm - margins.bottomMm
                if (fitsCurrentPage || timeline || !fitsFreshPage) {
                    if (!fitsCurrentPage && cursorYMm > margins.topMm && fitsFreshPage) newPage()
                    val offset = cursorYMm
                    val placed = blocksRel.map { offsetBlock(it, xMm, offset) }
                    place(placed, heightMm + ENTRY_GAP_MM)
                } else {
                    // Doesn't fit what's left of this page, but would fit a fresh page as a whole, and
                    // has no timeline decoration to keep intact — rather than moving the entire entry
                    // (title + every bullet) to the next page and leaving whatever room is left here
                    // blank, split it: the title/subtitle stays glued to the first body line so it's
                    // never orphaned alone at a page bottom, and the remaining bullets flow onto the
                    // next page(s) only as needed, filling the leftover space instead of wasting it.
                    val split = measureEntrySplit(entry, widthMm, density.baseFontSizePt, density.lineHeightMultiplier, bodyFont, theme.secondaryColor, theme.accentColor)
                    val headTotal = split.headHeightMm + split.bodyLeadGapMm
                    val firstLineHeight = split.bodyLines.firstOrNull()?.second ?: 0f
                    if (headTotal + firstLineHeight > remaining() && cursorYMm > margins.topMm) newPage()
                    place(split.headBlocks.map { offsetBlock(it, xMm, cursorYMm) }, headTotal)
                    for ((lineBlocks, lineHeightMm) in split.bodyLines) {
                        if (lineHeightMm > remaining() && cursorYMm > margins.topMm) newPage()
                        place(lineBlocks.map { offsetBlock(it, xMm, cursorYMm) }, lineHeightMm)
                    }
                    cursorYMm += ENTRY_GAP_MM
                }
            }
            cursorYMm += density.sectionSpacingMm - ENTRY_GAP_MM
        }
        return pages
    }

    private fun buildHeader(
        resume: ResumeWithDetails, layout: LayoutDefinition, xMm: Float, yMm: Float, widthMm: Float,
        headingFont: FontRef, bodyFont: FontRef, accent: ColorRef, sizeScale: Float = 1f,
    ): Pair<List<PositionedBlock>, Float> {
        val nameSizePt = 20f * sizeScale
        val designationSizePt = 12f * sizeScale
        val contactSizePt = 9.5f * sizeScale
        val isColorBlock = layout.headerStyle == HeaderStyle.COLOR_BLOCK
        val textColor = if (isColorBlock) ColorRef("#FFFFFF") else accent
        val padTop = if (isColorBlock) 6f else 0f
        val padLeft = if (isColorBlock) 4f else 0f

        // The photo (when present) is always drawn at the header's right edge (see photoBlock call
        // below), so the text column must stay clear of that same right-hand strip — reserving the
        // space on the left instead (as before) left text starting needlessly indented while its
        // right edge still ran under the photo, clipping the last contact field against the page edge.
        val photoInHeader = layout.photoSlot != null && layout.photoSlot.inHeader
        val photoWidth = if (photoInHeader) (layout.photoSlot?.sizeMm ?: 0f) + 6f else 0f
        val textXMm = xMm + padLeft
        val textWidthMm = widthMm - padLeft * 2 - photoWidth
        val textRightMm = textXMm + textWidthMm

        val blocks = mutableListOf<PositionedBlock>()
        var y = yMm + padTop
        // Name/designation are single TextLines with no CSS wrapping of their own (white-space:pre) —
        // a long name or job title next to a header photo needs the same word-wrap-to-width
        // treatment as the contact line below, or it just runs under the photo unclipped.
        val nameLineHeight = textMeasurer.lineHeightMm(nameSizePt, 1.2f)
        for (line in textMeasurer.wrapToWidth(resume.resume.name, headingFont, nameSizePt, textWidthMm)) {
            blocks += PositionedBlock.TextLine(line, textXMm, y, textWidthMm, headingFont.family, headingFont.assetPath, nameSizePt, textColor, TextStyleFlags(bold = true))
            y += nameLineHeight
        }
        if (resume.resume.designation.isNotBlank()) {
            val designationLineHeight = textMeasurer.lineHeightMm(designationSizePt, 1.2f)
            for (line in textMeasurer.wrapToWidth(resume.resume.designation, bodyFont, designationSizePt, textWidthMm)) {
                blocks += PositionedBlock.TextLine(line, textXMm, y, textWidthMm, bodyFont.family, bodyFont.assetPath, designationSizePt, textColor)
                y += designationLineHeight
            }
        }
        val contactFields = listOf(resume.resume.email, resume.resume.phone, resume.resume.address).filter { it.isNotBlank() }
        if (contactFields.isNotEmpty()) {
            val contactLineHeight = textMeasurer.lineHeightMm(contactSizePt, 1.2f)
            if (layout.iconPolicy == co.resume.domain.layout.IconPolicy.INLINE_WITH_TEXT) {
                val prefixWidth = ICON_DOT_MM + 1.5f
                var cx = textXMm
                for ((i, field) in contactFields.withIndex()) {
                    // Word-wrap each field against the FULL available width (not just what's left
                    // on the current line) — a single long field like a street address can exceed
                    // the whole column width by itself, and packing fields onto a line only helps
                    // once each field individually fits; without this a long address just ran off
                    // the page edge (or under the photo) as one unbroken, unclipped line.
                    val fieldLines = textMeasurer.wrapToWidth(field, bodyFont, contactSizePt, textWidthMm - prefixWidth)
                    for ((lineIndex, lineText) in fieldLines.withIndex()) {
                        val lineWidth = textMeasurer.stringWidthMm(lineText, bodyFont, contactSizePt)
                        val needsFreshLine = lineIndex > 0 || (cx > textXMm && cx + prefixWidth + lineWidth > textRightMm)
                        if (needsFreshLine) {
                            y += contactLineHeight
                            cx = textXMm
                        }
                        if (lineIndex == 0) {
                            blocks += PositionedBlock.IconGlyph("dot", cx, y + contactSizePt * 0.32f, ICON_DOT_MM, textColor)
                        }
                        val fieldXMm = cx + prefixWidth
                        blocks += PositionedBlock.TextLine(lineText, fieldXMm, y, textRightMm - fieldXMm, bodyFont.family, bodyFont.assetPath, contactSizePt, textColor)
                        cx = fieldXMm + lineWidth + 5f
                    }
                    if (i < contactFields.lastIndex) cx += 2f
                }
                y += contactLineHeight
            } else {
                val lines = textMeasurer.wrapToWidth(contactFields.joinToString("   •   "), bodyFont, contactSizePt, textWidthMm)
                for (line in lines) {
                    blocks += PositionedBlock.TextLine(line, textXMm, y, textWidthMm, bodyFont.family, bodyFont.assetPath, contactSizePt, textColor)
                    y += contactLineHeight
                }
            }
        }
        if (photoInHeader && layout.photoSlot != null) {
            val (photoBlocks, _) = photoBlock(resume, layout.photoSlot, xMm + widthMm - layout.photoSlot.sizeMm, yMm + padTop, layout.photoSlot.sizeMm)
            blocks += photoBlocks
        }
        return blocks to (y - yMm + padTop)
    }

    private fun photoBlock(resume: ResumeWithDetails, slot: co.resume.domain.layout.PhotoSlot, xMm: Float, yMm: Float, widthMm: Float): Pair<List<PositionedBlock>, Float> {
        val path = resume.resume.profilePhotoPath ?: return emptyList<PositionedBlock>() to 0f
        val sizeMm = minOf(widthMm, slot.sizeMm)
        val clip = when (slot.shape) {
            PhotoShape.CIRCLE -> ClipShape.CIRCLE
            PhotoShape.ROUNDED_SQUARE -> ClipShape.ROUNDED_RECT
            PhotoShape.SQUARE -> ClipShape.NONE
        }
        return listOf(PositionedBlock.Image(RectMm(xMm, yMm, sizeMm, sizeMm), path, clip)) to sizeMm
    }

    private fun headingBlocks(
        text: String, xMm: Float, yMm: Float, widthMm: Float, sizePt: Float,
        font: FontRef, accent: ColorRef, style: HeadingStyle,
    ): List<PositionedBlock> {
        val barIndent = if (style == HeadingStyle.ACCENT_BAR) 3.5f else 0f
        val textBlock = PositionedBlock.TextLine(
            text = text.uppercase(),
            xMm = xMm + barIndent, yMm = yMm, widthMm = widthMm - barIndent,
            fontFamily = font.family, fontAssetPath = font.assetPath, sizePt = sizePt,
            color = accent, style = TextStyleFlags(bold = true),
        )
        // One consistent heading treatment per layout (requirement: a single, clearly labeled style throughout).
        val decoration = when (style) {
            HeadingStyle.UPPERCASE_UNDERLINE -> listOf(PositionedBlock.Rule(xMm, yMm + textMeasurer.lineHeightMm(sizePt, 1.1f), widthMm, 0.75f, accent))
            HeadingStyle.ACCENT_BAR -> listOf(PositionedBlock.Rect(RectMm(xMm, yMm + 0.5f, 2f, textMeasurer.lineHeightMm(sizePt, 0.9f)), accent))
            HeadingStyle.SMALL_CAPS_DIVIDER -> listOf(PositionedBlock.Rule(xMm, yMm + textMeasurer.lineHeightMm(sizePt, 1.15f), widthMm, 0.4f, accent))
            HeadingStyle.BOLD_CAPS -> emptyList()
        }
        return listOf(textBlock) + decoration
    }

    /** Measures one entry into relative blocks (yMm measured from the entry's own top, xMm from column left = 0). */
    private fun measureEntry(
        entry: SectionEntry, widthMm: Float, bodySizePt: Float, lineHeightMultiplier: Float,
        bodyFont: FontRef, textColor: ColorRef, accent: ColorRef, timelineMarker: Boolean,
    ): Pair<List<PositionedBlock>, Float> {
        val indent = if (timelineMarker) TIMELINE_INDENT_MM else 0f
        val innerWidth = widthMm - indent
        val blocks = mutableListOf<PositionedBlock>()
        var y = 0f
        val lineHeight = textMeasurer.lineHeightMm(bodySizePt, lineHeightMultiplier)

        // Title/subtitle are plain (unwrapped-by-CSS) TextLines — a long university/company name
        // plus date range easily exceeds a narrow sidebar column's width and, with nothing to wrap
        // it, bled unclipped into the neighboring main column. Word-wrap both the same way the
        // header line already is.
        if (entry.titleLine != null) {
            for (line in textMeasurer.wrapToWidth(entry.titleLine, bodyFont, bodySizePt + 0.5f, innerWidth)) {
                blocks += PositionedBlock.TextLine(line, indent, y, innerWidth, bodyFont.family, bodyFont.assetPath, bodySizePt + 0.5f, textColor, TextStyleFlags(bold = true), entryId = entry.id)
                y += lineHeight
            }
        }
        if (entry.subtitleLine != null || entry.dateLine != null) {
            val line = listOfNotNull(entry.subtitleLine, entry.dateLine).joinToString("   •   ")
            for (wrapped in textMeasurer.wrapToWidth(line, bodyFont, bodySizePt - 0.5f, innerWidth)) {
                blocks += PositionedBlock.TextLine(wrapped, indent, y, innerWidth, bodyFont.family, bodyFont.assetPath, bodySizePt - 0.5f, accent, entryId = entry.id)
                y += lineHeight
            }
        }
        if (entry.body.isNotEmpty()) {
            if (blocks.isNotEmpty()) y += BODY_GAP_MM
            for (richBlock in entry.body) {
                val (bodyBlocks, bodyHeight) = measureRichBlock(richBlock, innerWidth, bodySizePt, lineHeightMultiplier, bodyFont, textColor, entry.id)
                blocks += bodyBlocks.map { offsetBlock(it, indent, y) }
                y += bodyHeight
            }
        }
        if (timelineMarker) {
            blocks += PositionedBlock.IconGlyph("dot", 0.5f, 1.2f, 2.4f, accent, entry.id)
            // Thin vertical connector down to the entry's bottom, drawn as a slim filled rect (renderers have no dedicated vertical-line primitive).
            blocks += PositionedBlock.Rect(RectMm(1.6f, 3.5f, 0.4f, max(0f, y - 3.5f)), accent)
        }
        return blocks to y
    }

    private data class EntrySplit(
        val headBlocks: List<PositionedBlock>,
        val headHeightMm: Float,
        val bodyLeadGapMm: Float,
        val bodyLines: List<Pair<List<PositionedBlock>, Float>>,
    )

    /** Same content as [measureEntry] but with each wrapped body line kept separate (not pre-summed
     * into one atomic height) so the caller can place lines individually and break pages between
     * them. Never used for timeline entries — their dot/connector decoration assumes one atomic block. */
    private fun measureEntrySplit(
        entry: SectionEntry, widthMm: Float, bodySizePt: Float, lineHeightMultiplier: Float,
        bodyFont: FontRef, textColor: ColorRef, accent: ColorRef,
    ): EntrySplit {
        val lineHeight = textMeasurer.lineHeightMm(bodySizePt, lineHeightMultiplier)
        val headBlocks = mutableListOf<PositionedBlock>()
        var headHeight = 0f
        if (entry.titleLine != null) {
            for (line in textMeasurer.wrapToWidth(entry.titleLine, bodyFont, bodySizePt + 0.5f, widthMm)) {
                headBlocks += PositionedBlock.TextLine(line, 0f, headHeight, widthMm, bodyFont.family, bodyFont.assetPath, bodySizePt + 0.5f, textColor, TextStyleFlags(bold = true), entryId = entry.id)
                headHeight += lineHeight
            }
        }
        if (entry.subtitleLine != null || entry.dateLine != null) {
            val line = listOfNotNull(entry.subtitleLine, entry.dateLine).joinToString("   •   ")
            for (wrapped in textMeasurer.wrapToWidth(line, bodyFont, bodySizePt - 0.5f, widthMm)) {
                headBlocks += PositionedBlock.TextLine(wrapped, 0f, headHeight, widthMm, bodyFont.family, bodyFont.assetPath, bodySizePt - 0.5f, accent, entryId = entry.id)
                headHeight += lineHeight
            }
        }
        val bodyLeadGap = if (headBlocks.isNotEmpty() && entry.body.isNotEmpty()) BODY_GAP_MM else 0f
        val bodyLines = mutableListOf<Pair<List<PositionedBlock>, Float>>()
        for (richBlock in entry.body) {
            val (bodyBlocks, _) = measureRichBlock(richBlock, widthMm, bodySizePt, lineHeightMultiplier, bodyFont, textColor, entry.id)
            for (lineBlock in bodyBlocks) {
                val line = lineBlock as PositionedBlock.TextLine
                bodyLines += listOf<PositionedBlock>(line.copy(yMm = 0f)) to lineHeight
            }
        }
        return EntrySplit(headBlocks, headHeight, bodyLeadGap, bodyLines)
    }

    private fun measureRichBlock(
        block: RichBlock, widthMm: Float, sizePt: Float, lineHeightMultiplier: Float,
        font: FontRef, color: ColorRef, entryId: String?,
    ): Pair<List<PositionedBlock>, Float> {
        val lineHeight = textMeasurer.lineHeightMm(sizePt, lineHeightMultiplier)
        val (prefix, spans, availableWidth, indentMm) = when (block) {
            is RichBlock.Paragraph -> Quad("", block.spans, widthMm, 0f)
            is RichBlock.BulletItem -> Quad("•  ", block.spans, widthMm - BULLET_INDENT_MM, BULLET_INDENT_MM)
            is RichBlock.NumberedItem -> Quad("${block.index}.  ", block.spans, widthMm - BULLET_INDENT_MM, BULLET_INDENT_MM)
        }
        val plainText = prefix + spans.joinToString("") { it.text }
        val style = TextStyleFlags(bold = spans.size == 1 && spans[0].bold, italic = spans.size == 1 && spans[0].italic)
        val lines = textMeasurer.wrapToWidth(plainText, font, sizePt, availableWidth)
        val blocks = lines.mapIndexed { i, line ->
            PositionedBlock.TextLine(line, indentMm, i * lineHeight, availableWidth, font.family, font.assetPath, sizePt, color, style, entryId)
        }
        return blocks to (lines.size * lineHeight)
    }

    private data class Quad(val prefix: String, val spans: List<RichSpan>, val widthMm: Float, val indentMm: Float)

    private fun footerFor(
        layout: LayoutDefinition, pageNumber: Int, totalPages: Int, pageSize: SizeMm, margins: MarginsMm,
        font: FontRef, color: ColorRef, sizeScale: Float = 1f,
    ): PositionedBlock.TextLine? {
        if (layout.footerStyle == FooterStyle.NONE) return null
        val text = "Page $pageNumber of $totalPages"
        val sizePt = 8.5f * sizeScale
        val yMm = pageSize.heightMm - margins.bottomMm + 6f
        val fullWidthMm = pageSize.widthMm - margins.leftMm - margins.rightMm
        val textWidthMm = textMeasurer.stringWidthMm(text, font, sizePt)
        val xMm = when (layout.footerStyle) {
            FooterStyle.PAGE_NUMBER_RIGHT -> margins.leftMm + fullWidthMm - textWidthMm
            FooterStyle.PAGE_NUMBER_CENTER -> margins.leftMm + (fullWidthMm - textWidthMm) / 2f
            FooterStyle.NONE -> margins.leftMm
        }
        return PositionedBlock.TextLine(text, xMm, yMm, textWidthMm, font.family, font.assetPath, sizePt, color)
    }

    private fun tint(color: ColorRef): ColorRef {
        // A light, low-opacity-looking tint of the accent color for sidebar backgrounds, without needing real alpha compositing.
        val clean = color.hex.removePrefix("#")
        val r = clean.substring(0, 2).toInt(16)
        val g = clean.substring(2, 4).toInt(16)
        val b = clean.substring(4, 6).toInt(16)
        fun lighten(c: Int) = (c + (255 - c) * 0.88f).toInt().coerceIn(0, 255)
        return ColorRef("#%02X%02X%02X".format(lighten(r), lighten(g), lighten(b)))
    }

    private fun offsetBlock(block: PositionedBlock, dx: Float, dy: Float): PositionedBlock = when (block) {
        is PositionedBlock.TextLine -> block.copy(xMm = block.xMm + dx, yMm = block.yMm + dy)
        is PositionedBlock.Rule -> block.copy(xMm = block.xMm + dx, yMm = block.yMm + dy)
        is PositionedBlock.Rect -> block.copy(boundsMm = block.boundsMm.copy(xMm = block.boundsMm.xMm + dx, yMm = block.boundsMm.yMm + dy))
        is PositionedBlock.Image -> block.copy(boundsMm = block.boundsMm.copy(xMm = block.boundsMm.xMm + dx, yMm = block.boundsMm.yMm + dy))
        is PositionedBlock.IconGlyph -> block.copy(xMm = block.xMm + dx, yMm = block.yMm + dy)
    }

    companion object {
        private val TIMELINE_SECTION_TYPES = setOf(co.resume.domain.layout.SectionType.WORK_EXPERIENCE, co.resume.domain.layout.SectionType.EDUCATION)
    }
}
