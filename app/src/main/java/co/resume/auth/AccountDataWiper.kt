package co.resume.auth

import android.content.Context
import co.resume.data.local.AppDatabase
import co.resume.utils.SharedPref
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wipes everything the app stored locally for the deleted account — every resume/cover
 * letter/scan lives only on-device (see the onboarding "why we ask you to log in" page), so
 * deleting the Firebase account without also clearing this would leave all of it behind. Room's
 * database file and the app's private files/cache dirs are separate from anything Google Play
 * Billing knows about, so this intentionally does NOT and CANNOT touch a Play subscription —
 * that's cancelled by the user separately in Play, never something app code can reach.
 */
@Singleton
class AccountDataWiper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val sharedPref: SharedPref
) {
    suspend fun wipeAll() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        sharedPref.clear()
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().clear().apply()
        context.filesDir?.listFiles()?.forEach { it.deleteRecursively() }
        context.cacheDir?.listFiles()?.forEach { it.deleteRecursively() }
    }
}
