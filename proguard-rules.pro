# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/yxy/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-keep class com.iutiao.** { *; }
-libraryjars libs/Upay_Sdk_2.2.jar
-keep class com.upay.billing.** { *; }
-dontwarn com.upay.billing.**
-keep class cn.cmqame.sdk.** { *; }
-dontwarn cn.cmqame.sdk.**