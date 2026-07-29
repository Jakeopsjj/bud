# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep all generated composables
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep data classes
-keep class com.dialysis.app.ui.** { *; }
-keep class com.dialysis.app.ui.components.** { *; }

# Desugaring
-dontwarn java.lang.invoke.**
