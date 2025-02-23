package co.resume.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.multidex.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object VersionCheckUtils {

    // Replace with your actual package name
    private const val PACKAGE_NAME = "app.craft.myresume"

    // Check for newer version on the Play Store
    suspend fun isUpdateAvailable(context: Context): Boolean {
        return try {
            val currentVersion = BuildConfig.VERSION_NAME
            val latestVersion = getLatestVersionFromPlayStore() ?: return false
            // Compare version names
            latestVersion > currentVersion
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Fetch the latest version from the Play Store using Jsoup
    private suspend fun getLatestVersionFromPlayStore(): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = "https://play.google.com/store/apps/details?id=$PACKAGE_NAME"
                val document = Jsoup.connect(url).get()
                document.select("div[itemprop=softwareVersion]")
                    .firstOrNull()
                    ?.ownText()
                    ?.trim()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Launch Play Store for app update
    fun openPlayStore(context: Context) {
        val appPackageName = PACKAGE_NAME
        try {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            )
            context.startActivity(intent)
        }
    }
}
