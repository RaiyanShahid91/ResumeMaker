package co.resume.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.resume.utils.AppLanguage

/**
 * Bottom sheet listing every supported language — shared between Settings and the Login screen
 * (so users can pick their language before ever signing in) so both stay visually identical and
 * a language-list change only needs to happen once.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerSheet(
    title: String,
    currentLanguageCode: String,
    availableLanguages: List<AppLanguage>,
    onLanguageSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    AppBottomSheet(onDismissRequest = onDismissRequest, sheetState = sheetState) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            availableLanguages.forEach { lang ->
                LanguagePickerItem(
                    language = lang,
                    isSelected = lang.code == currentLanguageCode,
                    onClick = { onLanguageSelected(lang.code) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LanguagePickerItem(language: AppLanguage, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                language.nativeName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                language.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
