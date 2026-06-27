package co.resume.ui.screen.info

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context: Context = LocalContext.current
    val versionName = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrDefault("—")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── App header ──────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "ResumeAI",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "Version $versionName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        "Build a professional resume in minutes, on your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            // ── What is this app ────────────────────────────────────────────
            item {
                AboutSection(title = "What is ResumeAI?") {
                    Text(
                        "ResumeAI is a fully offline, privacy-first Android app that helps you create polished, professional resumes from scratch. Fill in your details, pick a template, customise the accent colour, and export a ready-to-share PDF — no account, no sign-up, and no data ever leaves your phone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Core features ───────────────────────────────────────────────
            item {
                AboutSection(title = "Core Features") {
                    val features = listOf(
                        FeatureItem(Icons.Filled.Person,      "Personal Details",    "Name, designation, email, phone, and address."),
                        FeatureItem(Icons.Filled.Psychology,  "Objective",           "Write or AI-generate a compelling career objective."),
                        FeatureItem(Icons.Filled.School,      "Education",           "Add multiple education entries with dates and grades."),
                        FeatureItem(Icons.Filled.Work,        "Work Experience",     "Log jobs with AI-improved bullet-point descriptions."),
                        FeatureItem(Icons.Filled.Star,        "Skills",              "Add skills manually or let AI suggest 10 relevant ones."),
                        FeatureItem(Icons.Filled.Description, "Projects",            "Showcase projects with links and AI-generated summaries."),
                        FeatureItem(Icons.Filled.Star,        "Achievements",        "Highlight awards and accomplishments, AI-assisted."),
                        FeatureItem(Icons.Filled.Language,    "Languages & More",    "Languages, interests, hobbies, and a declaration section."),
                        FeatureItem(Icons.Filled.Person,      "Profile Photo",       "Attach a profile photo and signature directly from your camera or gallery."),
                    )
                    features.forEach { FeatureRow(it) }
                }
            }

            // ── Templates & Design ──────────────────────────────────────────
            item {
                AboutSection(title = "Templates & Design") {
                    val items = listOf(
                        FeatureItem(Icons.Filled.GridView,   "13 Templates",        "Classic, Modern, Minimal, Bold, Elegant, Creative, Two-Column, Academic, Developer, Designer, Portfolio, Teacher, Code Dark."),
                        FeatureItem(Icons.Filled.ColorLens,  "Accent Colour",       "Pick any colour — it applies to your header, section titles, and dividers instantly."),
                        FeatureItem(Icons.Filled.Download,   "PDF Export",          "Export your resume as a high-quality PDF, ready to send to employers."),
                    )
                    items.forEach { FeatureRow(it) }
                }
            }

            // ── AI features ─────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "AI-Powered Features",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "The app integrates an AI assistant to help you write and refine resume content. Tap the ✨ sparkle icon inside any supported section to generate or improve text instantly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(12.dp))
                        val aiFeatures = listOf(
                            FeatureItem(Icons.Filled.AutoAwesome, "Objective Generator",   "Writes a professional career objective tailored to your designation."),
                            FeatureItem(Icons.Filled.Work,        "Job Description AI",    "Rewrites work experience bullet points with strong action verbs."),
                            FeatureItem(Icons.Filled.Star,        "Skill Suggestions",     "Suggests 10 relevant skills based on your job role."),
                            FeatureItem(Icons.Filled.Description, "Project Summaries",     "Generates concise project descriptions from just a project name."),
                            FeatureItem(Icons.Filled.Star,        "Achievement Writer",    "Crafts impactful, quantified achievement statements."),
                            FeatureItem(Icons.Filled.Chat,        "AI Chat Assistant",     "Open-ended chat to brainstorm, draft, or refine any part of your resume."),
                        )
                        aiFeatures.forEach { item ->
                            Row(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    item.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        item.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                    Text(
                                        item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Technology ──────────────────────────────────────────────────
            item {
                AboutSection(title = "Technology") {
                    val tech = listOf(
                        "Built with" to "Kotlin & Jetpack Compose",
                        "UI toolkit" to "Material Design 3",
                        "Local storage" to "Room (SQLite) — all data stays on your device",
                        "AI model" to "Llama 3.1 8B Instant",
                        "AI provider" to "Groq Cloud API (fast inference)",
                        "PDF export" to "Android WebView → Print API",
                    )
                    tech.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(110.dp)
                            )
                            Text(
                                value,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Privacy ─────────────────────────────────────────────────────
            item {
                AboutSection(title = "Privacy") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Your resume data is stored locally on your device using an encrypted database. It is never uploaded or shared with any server. The only time the app accesses the internet is when you use an AI feature — your text prompt is sent to Groq's API to generate a response and nothing else is transmitted.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Contact ─────────────────────────────────────────────────────
            item {
                AboutSection(title = "Contact & Support") {
                    Text(
                        "Have a question, found a bug, or want to suggest a feature? We'd love to hear from you. Go to Settings → Write Us to send us an email.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private data class FeatureItem(val icon: ImageVector, val title: String, val description: String)

@Composable
private fun AboutSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp, bottom = 10.dp)
        )
        content()
        HorizontalDivider(modifier = Modifier.padding(top = 14.dp))
    }
}

@Composable
private fun FeatureRow(item: FeatureItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(6.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(
                item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
