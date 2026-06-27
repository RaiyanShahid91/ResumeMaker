package co.resume.ui.screen.dashboard

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import co.resumeai.R

private enum class DashboardTab(@StringRes val labelRes: Int) {
    Home(R.string.nav_home),
    MyResume(R.string.nav_my_resume),
    Settings(R.string.nav_settings)
}

@Composable
fun DashboardScreen(
    onOpenResumeEditor: (resumeId: Long) -> Unit,
    onBrowseTemplates: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenAiChat: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showCreateSheet by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                DashboardTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                when (tab) {
                                    DashboardTab.Home -> Icons.Filled.Home
                                    DashboardTab.MyResume -> Icons.Filled.Description
                                    DashboardTab.Settings -> Icons.Filled.Settings
                                },
                                contentDescription = stringResource(tab.labelRes)
                            )
                        },
                        label = { Text(stringResource(tab.labelRes)) }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == DashboardTab.MyResume.ordinal) {
                FloatingActionButton(onClick = { showCreateSheet++ }) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.nav_create_resume))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (DashboardTab.entries[selectedTab]) {
                DashboardTab.Home -> HomeTab(
                    onCreateResume = { showCreateSheet++ },
                    onViewResumes = { selectedTab = DashboardTab.MyResume.ordinal },
                    onBrowseTemplates = onBrowseTemplates,
                    onOpenAiChat = onOpenAiChat
                )
                DashboardTab.MyResume -> ResumeListTab(onOpenResume = onOpenResumeEditor)
                DashboardTab.Settings -> SettingsTab(
                    onLanguageSelected = onLanguageSelected,
                    onOpenPrivacy = onOpenPrivacy,
                    onOpenAbout = onOpenAbout,
                    onOpenGuide = onOpenGuide
                )
            }
        }
    }

    if (showCreateSheet > 0) {
        CreateResumeSheet(
            onDismiss = { showCreateSheet = 0 },
            onCreated = { id ->
                showCreateSheet = 0
                onOpenResumeEditor(id)
            }
        )
    }
}
