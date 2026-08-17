package co.resume.ui.screen.convert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.resumeai.R
import co.resume.ui.component.AppBottomSheet
import co.resume.ui.component.AppCard
import co.resume.ui.viewmodel.ConverterTool

private data class ConverterToolUi(
    val tool: ConverterTool,
    val icon: ImageVector,
    val title: String,
    val description: String
)

/** Bottom sheet listing the PDF-converter toolkit's tools, opened from the home screen's "Convert Files" tile. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterToolPickerSheet(
    onDismiss: () -> Unit,
    onSelectTool: (ConverterTool) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val tools = listOf(
        ConverterToolUi(ConverterTool.IMAGE_TO_PDF, Icons.Filled.Image, stringResource(R.string.converter_tool_image_to_pdf_title), stringResource(R.string.converter_tool_image_to_pdf_desc)),
        ConverterToolUi(ConverterTool.MERGE_PDF, Icons.Filled.MergeType, stringResource(R.string.converter_tool_merge_pdf_title), stringResource(R.string.converter_tool_merge_pdf_desc)),
        ConverterToolUi(ConverterTool.COMPRESS_PDF, Icons.Filled.Compress, stringResource(R.string.converter_tool_compress_pdf_title), stringResource(R.string.converter_tool_compress_pdf_desc)),
        ConverterToolUi(ConverterTool.PDF_TO_JPG, Icons.Filled.PictureAsPdf, stringResource(R.string.converter_tool_pdf_to_jpg_title), stringResource(R.string.converter_tool_pdf_to_jpg_desc))
    )
    val accents = listOf(primary, secondary, tertiary)

    AppBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
            Text(stringResource(R.string.converter_hub_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                stringResource(R.string.converter_hub_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            tools.forEachIndexed { index, toolUi ->
                val accent = accents[index % accents.size]
                AppCard(
                    onClick = { onSelectTool(toolUi.tool) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.6f)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(toolUi.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(toolUi.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(
                                toolUi.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
