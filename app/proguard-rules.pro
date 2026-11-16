# Настройки хранятся через kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.style.island.data.**$$serializer { *; }
-keepclassmembers class com.style.island.data.** {
    *** Companion;
}
-keepclasseswithmembers class com.style.island.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
