# 混淆文件

# keep dialogx
-keep class com.kongzue.dialogx.** { *; }
-dontwarn com.kongzue.dialogx.**

# 额外的，建议将 android.view 也列入 keep 范围：
-keep class android.view.** { *; }

# 若启用模糊效果，请增加如下配置：
-dontwarn androidx.renderscript.**
-keep public class androidx.renderscript.** { *; }