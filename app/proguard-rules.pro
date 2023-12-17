-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** w(...);
    public static *** v(...);
    public static *** i(...);
}

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# other
-keep public class * extends java.lang.Exception
-keep class ru.rznnike.eyehealthmanager.domain.model.** { <fields>; }
-keepclassmembers enum * { *; }
#noinspection ShrinkerUnresolvedReference
-keep class java.awt.datatransfer.DataFlavor {*;}
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
