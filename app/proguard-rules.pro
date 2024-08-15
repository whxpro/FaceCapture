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

-obfuscationdictionary ../opt/dict.txt
-classobfuscationdictionary ../opt/dict.txt
-packageobfuscationdictionary ../opt/dict.txt


-dontwarn com.live2d.sdk.cubism.framework.CubismDefaultParameterId$ParameterId
-dontwarn com.live2d.sdk.cubism.framework.CubismFramework$Option
-dontwarn com.live2d.sdk.cubism.framework.CubismFramework
-dontwarn com.live2d.sdk.cubism.framework.CubismFrameworkConfig$LogLevel
-dontwarn com.live2d.sdk.cubism.framework.CubismModelSettingJson
-dontwarn com.live2d.sdk.cubism.framework.ICubismModelSetting
-dontwarn com.live2d.sdk.cubism.framework.effect.CubismBreath$BreathParameterData
-dontwarn com.live2d.sdk.cubism.framework.effect.CubismBreath
-dontwarn com.live2d.sdk.cubism.framework.effect.CubismEyeBlink
-dontwarn com.live2d.sdk.cubism.framework.effect.CubismPose
-dontwarn com.live2d.sdk.cubism.framework.id.CubismId
-dontwarn com.live2d.sdk.cubism.framework.id.CubismIdManager
-dontwarn com.live2d.sdk.cubism.framework.math.CubismMatrix44
-dontwarn com.live2d.sdk.cubism.framework.math.CubismModelMatrix
-dontwarn com.live2d.sdk.cubism.framework.math.CubismTargetPoint
-dontwarn com.live2d.sdk.cubism.framework.math.CubismViewMatrix
-dontwarn com.live2d.sdk.cubism.framework.model.CubismModel
-dontwarn com.live2d.sdk.cubism.framework.model.CubismUserModel
-dontwarn com.live2d.sdk.cubism.framework.motion.ACubismMotion
-dontwarn com.live2d.sdk.cubism.framework.motion.CubismExpressionMotion
-dontwarn com.live2d.sdk.cubism.framework.motion.CubismExpressionMotionManager
-dontwarn com.live2d.sdk.cubism.framework.motion.CubismMotion
-dontwarn com.live2d.sdk.cubism.framework.motion.CubismMotionManager
-dontwarn com.live2d.sdk.cubism.framework.motion.IFinishedMotionCallback
-dontwarn com.live2d.sdk.cubism.framework.physics.CubismPhysics
-dontwarn com.live2d.sdk.cubism.framework.rendering.CubismRenderer
-dontwarn com.live2d.sdk.cubism.framework.rendering.android.CubismOffscreenSurfaceAndroid
-dontwarn com.live2d.sdk.cubism.framework.rendering.android.CubismRendererAndroid
-dontwarn com.live2d.sdk.cubism.framework.utils.CubismDebug