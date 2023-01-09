# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#############################################
#
# 项目中特殊处理部分
#
#############################################

#-----------处理js交互---------------
#-----------自定义控件---------------
#-----------处理反射类---------------
-keep class android.os.SystemProperties{*;}

#-----------处理实体类---------------
-keep class com.yingyongduoduo.ad.bean.**{*;}
-keep class com.yingyongduoduo.ad.event.**{*;}
-keep class com.yingyongduoduo.ad.interfaces.**{*;}

#-----------处理第三方依赖库---------

# Fresco
-keep class com.facebook.fresco.** {*;}
-keep interface com.facebook.fresco.** {*;}
-keep enum com.facebook.fresco.** {*;}

# Keep our interfaces so they can be used by other ProGuard rules.
# See http://sourceforge.net/p/proguard/bugs/466/
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
-keep,allowobfuscation @interface com.facebook.soloader.DoNotOptimize

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Do not strip any method/class that is annotated with @DoNotOptimize
-keep @com.facebook.soloader.DoNotOptimize class *
-keepclassmembers class * {
    @com.facebook.soloader.DoNotOptimize *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**


# Umeng友盟统计分析
-keep class com.umeng.** {*;}

-keep class org.repackage.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# EventBus
#-keepattributes *Annotation*
#-keepclassmembers class ** {
#    @org.greenrobot.eventbus.Subscribe <methods>;
#}
#-keep enum org.greenrobot.eventbus.ThreadMode { *; }

-keepattributes *Annotation*
-keepclassmembers class ** {
    @de.greenrobot.event.Subscribe <methods>;
}
-keep enum de.greenrobot.event.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

-keepclassmembers class ** {
    public void onEvent*(**);
}

# 广点通
-keep class com.qq.e.** {
    public protected *;
}


# 穿山甲
-keep class com.bytedance.pangle.** {*;}
-keep class com.bytedance.sdk.openadsdk.** { *; }

-keep class com.bykv.vk.** {*;}

-keep class com.ss.android.**{*;}
-keep class com.bytedance.android.**{*;}
-keep class com.byted.live.**{*;}

-keep class com.ad.android.** {*;}

-keepclassmembers class com.bytedance.android.alog.Alog {
    native <methods>;
}
-keepclassmembers class com.bytedance.android.alog.Alog {
    native <methods>;
}

