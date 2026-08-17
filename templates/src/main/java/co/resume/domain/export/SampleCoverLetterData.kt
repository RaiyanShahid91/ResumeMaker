package co.resume.domain.export

import co.resume.data.local.entity.CoverLetterEntity

/** Placeholder cover-letter content used only to render template previews — never persisted. */
object SampleCoverLetterData {

    val letter = CoverLetterEntity(
        id = -1L,
        title = "Sample Cover Letter",
        senderName = "Alex Morgan",
        senderEmail = "alex.morgan@email.com",
        senderPhone = "+1 555 010 2030",
        date = "January 1, 2026",
        recipientName = "Hiring Manager",
        companyName = "Northwind Labs",
        jobTitle = "Senior Product Designer",
        salutation = "Dear Hiring Manager,",
        bodyText = "I am excited to apply for the Senior Product Designer role at Northwind Labs. " +
            "With over six years of experience crafting user-centered web and mobile experiences, " +
            "I have led design for onboarding flows that increased activation by 22% and built " +
            "design systems adopted across a dozen product teams.\n\n" +
            "In my current role, I partner closely with product and engineering to ship polished, " +
            "accessible interfaces end-to-end, from research through launch. I led the redesign of " +
            "our core checkout experience, running a dozen usability studies that informed a flow " +
            "which lifted conversion by 14% within the first quarter of release, and I built and " +
            "still maintain the component library that now underpins every product surface across " +
            "the company, from marketing pages to the admin console.\n\n" +
            "Earlier in my career I worked across a mix of B2B and consumer products, which gave me " +
            "a broad view of how design decisions ripple through onboarding, retention, and support " +
            "costs alike. I care as much about the accessibility and performance of an interface as " +
            "its visual polish, and I have found that the strongest design systems are the ones " +
            "built in close partnership with the engineers who ship them, not handed off after the " +
            "fact.\n\n" +
            "I would welcome the opportunity to bring that same rigor, curiosity, and cross-" +
            "functional collaboration to your team, and I am confident the systems-first approach " +
            "I have honed over the last several years would help Northwind Labs scale its product " +
            "experience thoughtfully as the team grows.\n\n" +
            "Thank you for considering my application. I look forward to the possibility of " +
            "discussing how I can contribute to Northwind Labs.",
        closing = "Sincerely,\nAlex Morgan"
    )

    fun forTemplate(templateId: Int, accentColorHex: String? = null): CoverLetterEntity =
        letter.copy(templateId = templateId, accentColorHex = accentColorHex)
}
