// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.github.megatronking.stringfog.plugin.StringFogMode
import com.github.megatronking.stringfog.plugin.kg.RandomKeyGenerator

buildscript {
    dependencies {
        classpath("com.github.megatronking.stringfog:gradle-plugin:5.1.0")
        // 选用加解密算法库，默认实现了xor算法，也可以使用自己的加解密库。
        classpath("com.github.megatronking.stringfog:xor:5.0.0")

        classpath("com.github.liujingxing:XmlClassGuard:1.2.7")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

ext {
    set("stringfogRandomKeyGenerator", RandomKeyGenerator())

    set("stringfogMode", StringFogMode.base64)
}