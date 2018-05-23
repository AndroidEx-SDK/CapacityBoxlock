# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/androidex/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#压缩等级
-optimizationpasses 5
-dontusemixedcaseclassnames #【混淆时不会产生形形色色的类名 】
#【指定不去忽略非公共的库类。 】
-dontskipnonpubliclibraryclasses
#-dontpreverify 【不预校验】
#-verbose
#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#把混淆类中的方法名也混淆了
-useuniqueclassmembernames
#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification
#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
#保持泛型
-keepattributes Signature
#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao { public static java.lang.String TABLENAME; }
-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**

-printmapping mapping.txt #混淆后文件映射

#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------

#-------------------------------------------------------------------------
#---------------------------------2.第三方包-------------------------------

-dontwarn butterknife.internal.**

-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

#okhttp混淆配置
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class com.makeramen.** { *; }
-keep interface com.makeramen.** { *; }
-dontwarn com.makeramen.**

-keep class okio.** { *; }
-keep interface okio.** { *; }
-dontwarn okio.**

#-------------------------------------------------------------------------
#---------------------------------3.与js互相调用的类------------------------
#-------------------------------------------------------------------------
#---------------------------------4.反射相关的类和方法-----------------------
#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------
#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses -keepattributes Signature
-keepattributes SourceFile,LineNumberTable
 #----------------------------------------------------------------------------

 #---------------------------------默认保留区---------------------------------
 -keep public class * extends android.app.Activity
 -keep public class * extends android.app.Application
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.content.ContentProvider
 -keep public class * extends android.app.backup.BackupAgentHelper
 -keep public class * extends android.preference.Preference
 -keep public class * extends android.view.View
 -keep public class com.android.vending.licensing.ILicensingService
 -keep class android.support.** {*;}
 -keepclasseswithmembernames class * { native <methods>; }
 -keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
    }
 -keepclassmembers enum * {
    public static **[] values(); public static ** valueOf(java.lang.String); }
 -keep public class * extends android.view.View{
    *** get*(); void set*(***); public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    }
 -keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
     }
 -keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
    }
 -keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID; private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream); java.lang.Object writeReplace(); java.lang.Object readResolve();
  }
 -keep class **.R$* {
        *;
   }
 -keepclassmembers class * { void *(**On*Event); }
  #----------------------------------------------------------------------------

  #---------------------------------webview------------------------------------
 -keepclassmembers class fqcn.of.javascript.interface.for.Webview { public *; }
 -keepclassmembers class * extends android.webkit.WebViewClient {
       public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap); public boolean *(android.webkit.WebView, java.lang.String); }
 -keepclassmembers class * extends android.webkit.WebViewClient { public void *(android.webkit.WebView, jav.lang.String); }
 #---------------------------------------------------------------------------- #





