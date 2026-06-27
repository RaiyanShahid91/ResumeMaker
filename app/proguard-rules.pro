# Improve obfuscation by merging package namespaces
-flattenpackagehierarchy

# Strip source file names from stack traces in release builds
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Room — keep entity and DAO class members so the generated code works
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Dao interface * { *; }

# Hilt / Dagger generated code
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# AdMob SDK references LoudnessCodecController which was added in API 35 (compileSdk is 34)
-dontwarn android.media.LoudnessCodecController
-dontwarn android.media.LoudnessCodecController$OnLoudnessCodecUpdateListener

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
