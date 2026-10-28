# 包体精简（R8 + shrinkResources）下的必要保留规则。
# 这些类由 XML 或系统通过反射实例化，混淆/裁剪后名字对不上就会崩。

# 布局里的自定义 View：LayoutInflater 按全限定名反射构造
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# preference XML 里的自定义 Preference：PreferenceInflater 反射构造
-keep public class * extends androidx.preference.Preference {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends androidx.preference.DialogPreference {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keep public class * extends androidx.preference.PreferenceFragmentCompat

# Application / Service / Receiver 由系统按类名实例化
-keep class com.android.setup.service.** { *; }

# Settings 通过类名加载的 Overlay 控制器
-keep class com.android.setup.OverlayCategoryPreferenceController { *; }

# SharedPreferences 变更回调
-keepclassmembers class * implements android.content.SharedPreferences$OnSharedPreferenceChangeListener {
    public void onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String);
}

# hidden-api 是 compileOnly，运行时用系统实现，不要告警/裁剪
-dontwarn android.content.om.**
-dontwarn android.os.SystemProperties

# 保留注解信息（Refine / 序列化）
-keepattributes *Annotation*,InnerClasses,Signature

# 保留行号，方便线上堆栈定位
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
