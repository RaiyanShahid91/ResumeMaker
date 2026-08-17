package co.resume.domain.engine

import co.resume.data.local.entity.ResumeWithDetails
import co.resume.domain.layout.LayoutCatalog
import co.resume.domain.theme.FontCatalog
import co.resume.domain.theme.ThemeCatalog
import co.resume.domain.theme.withAccentOverride
import co.resume.domain.theme.withFontOverride
import javax.inject.Inject
import javax.inject.Singleton

/** Resolves a resume's stored layoutId/themeId/accentColorHex/fontFamilyId/fontSizeScale into the
 *  canonical ResumeLayout IR. Every renderer (preview WebView, PDF, DOCX) goes through this one
 *  place, so a font/size customization applies everywhere consistently rather than needing each
 *  export path to remember to read those fields itself. */
@Singleton
class ResumeLayoutBuilder @Inject constructor(private val layoutEngine: LayoutEngine) {

    fun build(resume: ResumeWithDetails): ResumeLayout {
        val layout = LayoutCatalog.resolve(resume.resume.layoutId)
        val theme = ThemeCatalog.resolve(resume.resume.themeId)
            .withAccentOverride(resume.resume.accentColorHex)
            .withFontOverride(FontCatalog.resolve(resume.resume.fontFamilyId)?.pairing)
        return layoutEngine.build(resume, layout, theme, sizeScale = resume.resume.fontSizeScale ?: 1f)
    }
}
