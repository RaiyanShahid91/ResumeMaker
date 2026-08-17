package co.resume.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Discriminates what produced a [ScannedDocumentEntity] row and which path field holds its file. */
object DocumentKind {
    const val SCAN = "scan"
    const val CONVERTED_PDF = "converted_pdf"
    const val CONVERTED_IMAGE = "converted_image"
}

/**
 * A saved document-scanner session — a multi-page scan exported as one PDF (e.g. an old
 * resume, certificate, ID) kept as a supporting file, independent of any single resume.
 *
 * Also doubles as the storage for the PDF-converter toolkit's output (image-to-PDF, merge,
 * compress, PDF-to-JPG) — see [kind]. For [DocumentKind.CONVERTED_IMAGE] rows, [pdfPath] is
 * blank and [imagePath] holds the file instead; every other kind uses [pdfPath].
 */
@Entity(tableName = "scanned_documents")
data class ScannedDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val pdfPath: String,
    val pageCount: Int,
    val ocrText: String,
    val needsReview: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    /** Newline-joined absolute paths to each persisted page image, in page order. */
    val pagePaths: String = "",
    val kind: String = DocumentKind.SCAN,
    /** Only set for [DocumentKind.CONVERTED_IMAGE] rows — a single converted JPG page. */
    val imagePath: String? = null
)
