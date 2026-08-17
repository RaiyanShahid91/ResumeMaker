package co.resume.domain.layout

private val standardOrder = listOf(
    SectionType.OBJECTIVE,
    SectionType.WORK_EXPERIENCE,
    SectionType.EDUCATION,
    SectionType.SKILLS,
    SectionType.PROJECTS,
    SectionType.ACHIEVEMENTS,
    SectionType.LANGUAGES,
    SectionType.INTERESTS,
    SectionType.HOBBIES,
    SectionType.DECLARATION,
)

private val sidebarFirstOrder = listOf(
    SectionType.OBJECTIVE,
    SectionType.WORK_EXPERIENCE,
    SectionType.PROJECTS,
    SectionType.ACHIEVEMENTS,
    SectionType.EDUCATION,
    SectionType.SKILLS,
    SectionType.LANGUAGES,
    SectionType.INTERESTS,
    SectionType.HOBBIES,
    SectionType.DECLARATION,
)

private val classicSidebarSections = setOf(SectionType.EDUCATION, SectionType.SKILLS, SectionType.LANGUAGES, SectionType.INTERESTS, SectionType.HOBBIES)

// Reused across the profession-oriented layouts below — narrower sidebar section sets than
// classicSidebarSections so the main column keeps the entry-heavy sections (work experience,
// projects) that need the extra width to avoid excessive wrapping.
private val skillsFocusedSidebar = setOf(SectionType.SKILLS, SectionType.LANGUAGES)
private val skillsEducationSidebar = setOf(SectionType.EDUCATION, SectionType.SKILLS, SectionType.LANGUAGES)
private val skillsPersonalitySidebar = setOf(SectionType.SKILLS, SectionType.LANGUAGES, SectionType.INTERESTS)

private val academicOrder = listOf(
    SectionType.OBJECTIVE, SectionType.EDUCATION, SectionType.WORK_EXPERIENCE, SectionType.PROJECTS,
    SectionType.ACHIEVEMENTS, SectionType.SKILLS, SectionType.LANGUAGES, SectionType.INTERESTS,
    SectionType.HOBBIES, SectionType.DECLARATION,
)

private val graduateOrder = listOf(
    SectionType.OBJECTIVE, SectionType.EDUCATION, SectionType.PROJECTS, SectionType.SKILLS,
    SectionType.WORK_EXPERIENCE, SectionType.ACHIEVEMENTS, SectionType.LANGUAGES, SectionType.INTERESTS,
    SectionType.HOBBIES, SectionType.DECLARATION,
)

/**
 * Strict, icon-free, single-column layout — the safe default for automated résumé parsers.
 * Reference layout for the shared engine (LayoutEngine/PdfExporter/HTML renderer are
 * validated against this one first, per the implementation plan, before the remaining 7 land).
 */
val AtsSafeLayout = LayoutDefinition(
    id = "ats_safe",
    title = "ATS Safe",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.UPPERCASE_UNDERLINE,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = standardOrder,
    supportsPersonalitySections = false,
    footerStyle = FooterStyle.NONE,
    isAtsSafe = true,
)

/** Single-column with a top header photo block. */
val PhotoHeaderLayout = LayoutDefinition(
    id = "photo_header",
    title = "Photo Header",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.PHOTO_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 24f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.UPPERCASE_UNDERLINE,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.35f, sectionSpacingMm = 4.5f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.NONE,
)

/** Two-column, contact/skills/education/etc. in a left sidebar. */
val SidebarLeftLayout = LayoutDefinition(
    id = "sidebar_left",
    title = "Sidebar Left",
    columns = listOf(
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f, hasBackground = true),
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
    ),
    headerStyle = HeaderStyle.TOP_BAR,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 26f, inHeader = false),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = classicSidebarSections,
    footerStyle = FooterStyle.NONE,
)

/** Two-column, sidebar on the right instead of the left. */
val SidebarRightLayout = SidebarLeftLayout.copy(
    id = "sidebar_right",
    title = "Sidebar Right",
    columns = listOf(
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f, hasBackground = true),
    ),
)

/** Dates rendered as visual markers down the left edge of Experience/Education. */
val TimelineLayout = LayoutDefinition(
    id = "timeline",
    title = "Timeline",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.TIMELINE_INLINE,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4.5f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.NONE,
    useTimelineMarkers = true,
)

/** Colored header band with name/title/photo. */
val ColorBlockHeaderLayout = LayoutDefinition(
    id = "color_block_header",
    title = "Color Block Header",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.COLOR_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.ROUNDED_SQUARE, sizeMm = 22f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.35f, sectionSpacingMm = 4.5f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.NONE,
)

/** Lots of white space, thin dividers. */
val MinimalModernLayout = LayoutDefinition(
    id = "minimal_modern",
    title = "Minimal Modern",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.SMALL_CAPS_DIVIDER,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.45f, sectionSpacingMm = 6f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.NONE,
)

/** Denser type scale/spacing so a long work history still fits reasonably few pages. */
val CompactDenseLayout = LayoutDefinition(
    id = "compact_dense",
    title = "Compact Dense",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.BOLD_CAPS,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 9f, lineHeightMultiplier = 1.15f, sectionSpacingMm = 2.5f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.PAGE_NUMBER_RIGHT,
)

// --- Profession-oriented layouts below: each is a fresh combination of column arrangement,
// header style, heading style, icon policy and density — not just a recolor of the 8 base
// layouts above — but every field is still one of the existing, already-battle-tested enum
// values/building blocks LayoutEngine already knows how to flow, measure and paginate. That's
// deliberate: introducing a genuinely new HeaderStyle/HeadingStyle would mean new rendering code
// in LayoutEngine + the HTML/PDF/DOCX renderers, each a fresh chance to reintroduce the same
// text-clipping/overlap bugs just fixed there. Recombining known-good primitives instead
// guarantees every one of these inherits that fix for free.

/** Dense, single-column, no photo — reads like a formal executive bio. */
val ExecutiveBriefLayout = LayoutDefinition(
    id = "executive_brief",
    title = "Executive Brief",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.BOLD_CAPS,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.25f, sectionSpacingMm = 4f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.PAGE_NUMBER_CENTER,
    isAtsSafe = true,
)

/** Sidebar built for a skills/stack list, not the classic contact-info sidebar. */
val TechStackLayout = LayoutDefinition(
    id = "tech_stack",
    title = "Tech Stack",
    columns = listOf(
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.32f, hasBackground = true),
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.68f),
    ),
    headerStyle = HeaderStyle.TOP_BAR,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 24f, inHeader = false),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsFocusedSidebar,
    footerStyle = FooterStyle.NONE,
)

/** Loose spacing, education-forward order, no photo — built for long-form CVs. */
val AcademicCvLayout = LayoutDefinition(
    id = "academic_cv",
    title = "Academic CV",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.SMALL_CAPS_DIVIDER,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.45f, sectionSpacingMm = 6f),
    defaultSectionOrder = academicOrder,
    footerStyle = FooterStyle.PAGE_NUMBER_CENTER,
)

/** Bold color-block header with a personality-forward sidebar. */
val CreativePortfolioLayout = LayoutDefinition(
    id = "creative_portfolio",
    title = "Creative Portfolio",
    columns = listOf(
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.64f),
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.36f, hasBackground = true),
    ),
    headerStyle = HeaderStyle.COLOR_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.ROUNDED_SQUARE, sizeMm = 22f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.35f, sectionSpacingMm = 4.5f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsPersonalitySidebar,
    footerStyle = FooterStyle.NONE,
)

/** Photo up top, underlined headings, page numbers — a polished single-column pitch. */
val SalesProLayout = LayoutDefinition(
    id = "sales_pro",
    title = "Sales Pro",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.PHOTO_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 24f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.UPPERCASE_UNDERLINE,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.PAGE_NUMBER_RIGHT,
)

/** Sidebar carries credentials/skills; clean underlined headings read as clinical/precise. */
val MedicalProfessionalLayout = LayoutDefinition(
    id = "medical_professional",
    title = "Medical Professional",
    columns = listOf(
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f, hasBackground = true),
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
    ),
    headerStyle = HeaderStyle.TOP_BAR,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 26f, inHeader = false),
    sectionHeadingStyle = HeadingStyle.UPPERCASE_UNDERLINE,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsEducationSidebar,
    footerStyle = FooterStyle.NONE,
)

/** No photo, dense, formal capitals — built for briefs and formal submissions. */
val LegalCounselLayout = LayoutDefinition(
    id = "legal_counsel",
    title = "Legal Counsel",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.BOLD_CAPS,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10f, lineHeightMultiplier = 1.25f, sectionSpacingMm = 3.5f),
    defaultSectionOrder = standardOrder,
    isAtsSafe = true,
)

/** Color-block header + timeline markers — built for a founder's narrative-driven work history. */
val StartupFounderLayout = LayoutDefinition(
    id = "startup_founder",
    title = "Startup Founder",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.COLOR_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.ROUNDED_SQUARE, sizeMm = 22f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.35f, sectionSpacingMm = 4.5f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.NONE,
    useTimelineMarkers = true,
)

/** Compact spacing, small-caps dividers, right sidebar — built to fit dense figures/metrics. */
val FinanceAnalystLayout = LayoutDefinition(
    id = "finance_analyst",
    title = "Finance Analyst",
    columns = listOf(
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f, hasBackground = true),
    ),
    headerStyle = HeaderStyle.TOP_BAR,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 24f, inHeader = false),
    sectionHeadingStyle = HeadingStyle.SMALL_CAPS_DIVIDER,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10f, lineHeightMultiplier = 1.25f, sectionSpacingMm = 3.5f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsEducationSidebar,
    footerStyle = FooterStyle.PAGE_NUMBER_RIGHT,
)

/** Photo header + left sidebar — a polished, client-facing professional-services look. */
val ConsultantLayout = LayoutDefinition(
    id = "consultant",
    title = "Consultant",
    columns = listOf(
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f, hasBackground = true),
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
    ),
    headerStyle = HeaderStyle.PHOTO_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 22f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsEducationSidebar,
    footerStyle = FooterStyle.NONE,
)

/** Strict single-column, no photo, page-numbered — the safest option for formal applications. */
val PublicSectorLayout = LayoutDefinition(
    id = "public_sector",
    title = "Public Sector",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.UPPERCASE_UNDERLINE,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.PAGE_NUMBER_CENTER,
    isAtsSafe = true,
)

/** Warm, welcoming photo header with a tinted personality sidebar. */
val HospitalityLayout = LayoutDefinition(
    id = "hospitality",
    title = "Hospitality",
    columns = listOf(
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.64f),
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.36f, hasBackground = true),
    ),
    headerStyle = HeaderStyle.PHOTO_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 24f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.35f, sectionSpacingMm = 4.5f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsPersonalitySidebar,
    footerStyle = FooterStyle.NONE,
)

/** Loose, unhurried single column — built for a teaching career's narrative. */
val TeacherEducatorLayout = LayoutDefinition(
    id = "teacher_educator",
    title = "Teacher / Educator",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.SMALL_CAPS_DIVIDER,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.4f, sectionSpacingMm = 5f),
    defaultSectionOrder = academicOrder,
    footerStyle = FooterStyle.NONE,
)

/** No photo, color-block header, timeline markers — built for a mission-driven work history. */
val NonprofitImpactLayout = LayoutDefinition(
    id = "nonprofit_impact",
    title = "Nonprofit Impact",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.COLOR_BLOCK,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.35f, sectionSpacingMm = 4.5f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.NONE,
    useTimelineMarkers = true,
)

/** Photo header + tinted left sidebar — an approachable, client-facing look. */
val RealEstateAgentLayout = LayoutDefinition(
    id = "real_estate_agent",
    title = "Real Estate Agent",
    columns = listOf(
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.32f, hasBackground = true),
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.68f),
    ),
    headerStyle = HeaderStyle.PHOTO_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.ROUNDED_SQUARE, sizeMm = 24f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.UPPERCASE_UNDERLINE,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsFocusedSidebar,
    footerStyle = FooterStyle.NONE,
)

/** No photo, generous white space, bold capitals — a portfolio-adjacent look for design work. */
val ArchitectDesignerLayout = LayoutDefinition(
    id = "architect_designer",
    title = "Architect / Designer",
    columns = listOf(
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f),
    ),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.BOLD_CAPS,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.4f, sectionSpacingMm = 5f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsPersonalitySidebar,
    footerStyle = FooterStyle.NONE,
)

/** Compact, sidebar-driven — built for a long tools/languages list next to project work. */
val DataScientistLayout = LayoutDefinition(
    id = "data_scientist",
    title = "Data Scientist",
    columns = listOf(
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f, hasBackground = true),
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
    ),
    headerStyle = HeaderStyle.TOP_BAR,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 22f, inHeader = false),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10f, lineHeightMultiplier = 1.25f, sectionSpacingMm = 3.5f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsEducationSidebar,
    footerStyle = FooterStyle.NONE,
)

/** No photo, tight spacing, right-aligned page numbers — built for a metrics-heavy support career. */
val CustomerSupportLeadLayout = LayoutDefinition(
    id = "customer_support_lead",
    title = "Customer Support Lead",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.MINIMAL,
    photoSlot = null,
    sectionHeadingStyle = HeadingStyle.UPPERCASE_UNDERLINE,
    iconPolicy = IconPolicy.NONE,
    densityPreset = DensityPreset(baseFontSizePt = 10f, lineHeightMultiplier = 1.25f, sectionSpacingMm = 3.5f),
    defaultSectionOrder = standardOrder,
    footerStyle = FooterStyle.PAGE_NUMBER_RIGHT,
)

/** Small photo, generous spacing, education-forward order — built to fill the page for a shorter, early-career resume. */
val GraduateEntryLevelLayout = LayoutDefinition(
    id = "graduate_entry_level",
    title = "Graduate / Entry-Level",
    columns = listOf(ColumnSpec(role = ColumnRole.MAIN, widthFraction = 1f)),
    headerStyle = HeaderStyle.PHOTO_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.CIRCLE, sizeMm = 20f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.SMALL_CAPS_DIVIDER,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.45f, sectionSpacingMm = 6f),
    defaultSectionOrder = graduateOrder,
    footerStyle = FooterStyle.NONE,
)

/** Color-block header, timeline markers, right sidebar — built for a project/gig work history. */
val FreelanceConsultantLayout = LayoutDefinition(
    id = "freelance_consultant",
    title = "Freelance Consultant",
    columns = listOf(
        ColumnSpec(role = ColumnRole.MAIN, widthFraction = 0.66f),
        ColumnSpec(role = ColumnRole.SIDEBAR, widthFraction = 0.34f, hasBackground = true),
    ),
    headerStyle = HeaderStyle.COLOR_BLOCK,
    photoSlot = PhotoSlot(shape = PhotoShape.ROUNDED_SQUARE, sizeMm = 22f, inHeader = true),
    sectionHeadingStyle = HeadingStyle.ACCENT_BAR,
    iconPolicy = IconPolicy.INLINE_WITH_TEXT,
    densityPreset = DensityPreset(baseFontSizePt = 10.5f, lineHeightMultiplier = 1.3f, sectionSpacingMm = 4f),
    defaultSectionOrder = sidebarFirstOrder,
    sidebarSections = skillsFocusedSidebar,
    footerStyle = FooterStyle.NONE,
    useTimelineMarkers = true,
)

/** Same design with SIDEBAR/MAIN swapped to the other side — every other field carried over as-is.
 * Backs the preview dialog's Left/Right position toggle (see [CuratedTemplateOption.mirrorLayoutId])
 * for every template whose column list actually has a sidebar, the same way [SidebarRightLayout]
 * already mirrors [SidebarLeftLayout]. */
private fun LayoutDefinition.mirrored(): LayoutDefinition = copy(
    id = "${id}_mirrored",
    title = "$title (Mirrored)",
    columns = columns.reversed(),
)

val TechStackMirroredLayout = TechStackLayout.mirrored()
val CreativePortfolioMirroredLayout = CreativePortfolioLayout.mirrored()
val MedicalProfessionalMirroredLayout = MedicalProfessionalLayout.mirrored()
val FinanceAnalystMirroredLayout = FinanceAnalystLayout.mirrored()
val ConsultantMirroredLayout = ConsultantLayout.mirrored()
val HospitalityMirroredLayout = HospitalityLayout.mirrored()
val RealEstateAgentMirroredLayout = RealEstateAgentLayout.mirrored()
val ArchitectDesignerMirroredLayout = ArchitectDesignerLayout.mirrored()
val DataScientistMirroredLayout = DataScientistLayout.mirrored()
val FreelanceConsultantMirroredLayout = FreelanceConsultantLayout.mirrored()

/** Layouts registered with the engine, keyed by stable id (persisted on ResumeEntity.layoutId). */
object LayoutCatalog {
    val all: List<LayoutDefinition> = listOf(
        AtsSafeLayout, PhotoHeaderLayout, SidebarLeftLayout, SidebarRightLayout,
        TimelineLayout, ColorBlockHeaderLayout, MinimalModernLayout, CompactDenseLayout,
        ExecutiveBriefLayout, TechStackLayout, AcademicCvLayout, CreativePortfolioLayout,
        SalesProLayout, MedicalProfessionalLayout, LegalCounselLayout, StartupFounderLayout,
        FinanceAnalystLayout, ConsultantLayout, PublicSectorLayout, HospitalityLayout,
        TeacherEducatorLayout, NonprofitImpactLayout, RealEstateAgentLayout, ArchitectDesignerLayout,
        DataScientistLayout, CustomerSupportLeadLayout, GraduateEntryLevelLayout, FreelanceConsultantLayout,
        TechStackMirroredLayout, CreativePortfolioMirroredLayout, MedicalProfessionalMirroredLayout,
        FinanceAnalystMirroredLayout, ConsultantMirroredLayout, HospitalityMirroredLayout,
        RealEstateAgentMirroredLayout, ArchitectDesignerMirroredLayout, DataScientistMirroredLayout,
        FreelanceConsultantMirroredLayout,
    )

    fun resolve(layoutId: String): LayoutDefinition = all.firstOrNull { it.id == layoutId } ?: AtsSafeLayout
}
