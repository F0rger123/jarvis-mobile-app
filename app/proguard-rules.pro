# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/android-sdk/tools/proguard/proguard-android.txt

# Keep AIClient
-keep class com.jarvis.ai.api.** { *; }
-keep class com.jarvis.ai.data.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# JSON
-keep class org.json.** { *; }