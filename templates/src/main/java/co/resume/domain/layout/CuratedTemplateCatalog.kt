package co.resume.domain.layout

import co.resume.domain.export.TemplateCategory
import co.resume.domain.theme.AcademicBurgundyTheme
import co.resume.domain.theme.CharcoalGoldTheme
import co.resume.domain.theme.CreativeCoralTheme
import co.resume.domain.theme.CrimsonTheme
import co.resume.domain.theme.EmeraldTheme
import co.resume.domain.theme.FinanceForestTheme
import co.resume.domain.theme.HospitalityRoseTheme
import co.resume.domain.theme.LegalNavyTheme
import co.resume.domain.theme.MedicalBlueTheme
import co.resume.domain.theme.NonprofitAmberTheme
import co.resume.domain.theme.PlumTheme
import co.resume.domain.theme.RealEstateClayTheme
import co.resume.domain.theme.SapphireTheme
import co.resume.domain.theme.SlateClassicTheme
import co.resume.domain.theme.StartupVioletTheme
import co.resume.domain.theme.TealBreezeTheme
import co.resume.domain.theme.TechIndigoTheme

/** A named, ready-made template a user picks — a curated (layout, theme) pair, not a raw builder. */
data class CuratedTemplateOption(
    val id: String,
    val title: String,
    val layoutId: String,
    val themeId: String,
    val category: TemplateCategory = TemplateCategory.Professional,
    /** Id of the mirror-image LayoutDefinition (same design, sidebar on the other side), if one
     * exists — lets the preview dialog offer a Left/Right position toggle instead of listing the
     * mirrored pair as two separate cards for what is otherwise the identical design. */
    val mirrorLayoutId: String? = null,
)

/**
 * One entry per structurally distinct LayoutDefinition — not per (layout, color) pair. Color is a
 * separate customization: the template preview dialog offers a color picker (see
 * InlineColorSwatchRow) that applies as ResumeEntity.accentColorHex on top of whichever of these a
 * user picks, the same way cover letter templates already work. Generating a separate catalog
 * entry per color was what made browsing feel repetitive — the same shape N times over — even
 * though the shapes themselves differ; one card per shape front-loads the real structural choice
 * and pushes color to where it belongs, as a lightweight tweak after.
 */
object CuratedTemplateCatalog {
    val templates: List<CuratedTemplateOption> = listOf(
        CuratedTemplateOption("ats_safe", "ATS Safe", AtsSafeLayout.id, SlateClassicTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("minimal_modern", "Minimal Modern", MinimalModernLayout.id, SapphireTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("photo_header", "Photo Header", PhotoHeaderLayout.id, SapphireTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("sidebar", "Sidebar", SidebarLeftLayout.id, SapphireTheme.id, TemplateCategory.Designer, mirrorLayoutId = SidebarRightLayout.id),
        CuratedTemplateOption("timeline", "Timeline", TimelineLayout.id, CrimsonTheme.id, TemplateCategory.Creative),
        CuratedTemplateOption("color_block_header", "Color Block Header", ColorBlockHeaderLayout.id, SapphireTheme.id, TemplateCategory.Creative),
        CuratedTemplateOption("compact_dense", "Compact Dense", CompactDenseLayout.id, SlateClassicTheme.id, TemplateCategory.Professional),

        // Profession-oriented templates — each pairs a structurally distinct LayoutDefinition
        // (see LayoutCatalog) with a theme picked for that field, rather than just recoloring one
        // of the 8 base shapes. Users can still repick color on any of them via the preview dialog.
        CuratedTemplateOption("executive_brief", "Executive Brief", ExecutiveBriefLayout.id, SlateClassicTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("tech_stack", "Tech Stack", TechStackLayout.id, TechIndigoTheme.id, TemplateCategory.Developer, mirrorLayoutId = TechStackMirroredLayout.id),
        CuratedTemplateOption("academic_cv", "Academic CV", AcademicCvLayout.id, AcademicBurgundyTheme.id, TemplateCategory.Teacher),
        CuratedTemplateOption("creative_portfolio", "Creative Portfolio", CreativePortfolioLayout.id, CreativeCoralTheme.id, TemplateCategory.Creative, mirrorLayoutId = CreativePortfolioMirroredLayout.id),
        CuratedTemplateOption("sales_pro", "Sales Pro", SalesProLayout.id, CrimsonTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("medical_professional", "Medical Professional", MedicalProfessionalLayout.id, MedicalBlueTheme.id, TemplateCategory.Professional, mirrorLayoutId = MedicalProfessionalMirroredLayout.id),
        CuratedTemplateOption("legal_counsel", "Legal Counsel", LegalCounselLayout.id, LegalNavyTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("startup_founder", "Startup Founder", StartupFounderLayout.id, StartupVioletTheme.id, TemplateCategory.Creative),
        CuratedTemplateOption("finance_analyst", "Finance Analyst", FinanceAnalystLayout.id, FinanceForestTheme.id, TemplateCategory.Professional, mirrorLayoutId = FinanceAnalystMirroredLayout.id),
        CuratedTemplateOption("consultant", "Consultant", ConsultantLayout.id, CharcoalGoldTheme.id, TemplateCategory.Professional, mirrorLayoutId = ConsultantMirroredLayout.id),
        CuratedTemplateOption("public_sector", "Public Sector", PublicSectorLayout.id, SlateClassicTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("hospitality", "Hospitality", HospitalityLayout.id, HospitalityRoseTheme.id, TemplateCategory.Professional, mirrorLayoutId = HospitalityMirroredLayout.id),
        CuratedTemplateOption("teacher_educator", "Teacher / Educator", TeacherEducatorLayout.id, TealBreezeTheme.id, TemplateCategory.Teacher),
        CuratedTemplateOption("nonprofit_impact", "Nonprofit Impact", NonprofitImpactLayout.id, NonprofitAmberTheme.id, TemplateCategory.Creative),
        CuratedTemplateOption("real_estate_agent", "Real Estate Agent", RealEstateAgentLayout.id, RealEstateClayTheme.id, TemplateCategory.Professional, mirrorLayoutId = RealEstateAgentMirroredLayout.id),
        CuratedTemplateOption("architect_designer", "Architect / Designer", ArchitectDesignerLayout.id, PlumTheme.id, TemplateCategory.Designer, mirrorLayoutId = ArchitectDesignerMirroredLayout.id),
        CuratedTemplateOption("data_scientist", "Data Scientist", DataScientistLayout.id, TechIndigoTheme.id, TemplateCategory.Developer, mirrorLayoutId = DataScientistMirroredLayout.id),
        CuratedTemplateOption("customer_support_lead", "Customer Support Lead", CustomerSupportLeadLayout.id, SapphireTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("graduate_entry_level", "Graduate / Entry-Level", GraduateEntryLevelLayout.id, EmeraldTheme.id, TemplateCategory.Professional),
        CuratedTemplateOption("freelance_consultant", "Freelance Consultant", FreelanceConsultantLayout.id, StartupVioletTheme.id, TemplateCategory.Creative, mirrorLayoutId = FreelanceConsultantMirroredLayout.id),
    )

    val categories: List<TemplateCategory> = TemplateCategory.entries.toList()

    private val byId = templates.associateBy { it.id }

    fun resolve(templateId: String): CuratedTemplateOption = byId[templateId] ?: templates.first()
}
