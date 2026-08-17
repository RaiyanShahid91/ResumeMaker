package co.resume.ui.screen.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.resumeai.R
import kotlinx.coroutines.launch

enum class DocumentsPage(@androidx.annotation.StringRes val labelRes: Int) {
    Resumes(R.string.my_resumes_tab_resumes),
    CoverLetters(R.string.my_resumes_tab_cover_letters),
    Scans(R.string.documents_tab_scans)
}

@Composable
fun DocumentsHubTab(
    onOpenResume: (resumeId: Long) -> Unit,
    onOpenCoverLetter: (id: Long) -> Unit,
    onOpenPdfViewer: (path: String, title: String) -> Unit,
    initialPage: Int = 0,
    onPageChanged: (Int) -> Unit = {}
) {
    val pagerState = rememberPagerState(initialPage = initialPage) { DocumentsPage.entries.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) { onPageChanged(pagerState.currentPage) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.nav_documents),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.Transparent
        ) {
            DocumentsPage.entries.forEachIndexed { index, page ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(stringResource(page.labelRes)) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (DocumentsPage.entries[page]) {
                DocumentsPage.Resumes -> ResumeListTab(onOpenResume = onOpenResume)
                DocumentsPage.CoverLetters -> CoverLetterListTab(onOpenCoverLetter = onOpenCoverLetter)
                DocumentsPage.Scans -> DocumentsListTab(onOpenPdfViewer = onOpenPdfViewer)
            }
        }
    }
}
