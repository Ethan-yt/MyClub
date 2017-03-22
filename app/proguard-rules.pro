# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Volumes/macFile/Files/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class cn.sharesdk.**{*;}
-keep class cn.smssdk.**{*;}
-keep class com.mob.**{*;}

-dontwarn com.mob.**
-dontwarn cn.smssdk.**
#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#Glide and OKHttp
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule