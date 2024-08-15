plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("xml-class-guard")
}
apply("${rootProject.projectDir}/opt/stringfog.gradle")

xmlClassGuard {
    //用于增量混淆的 mapping 文件
    val f = file("${project.buildDir.absolutePath}/intermediates/xml_class_guard/xml-class-mapping.txt")
    f.parentFile.mkdirs()
    mappingFile = f
}

apply("${rootProject.projectDir}/opt/xmlclassguard.gradle")

android {
    namespace = "com.facecapture.prod"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.facecapture.prod"
        minSdk = 26
        targetSdk = 34
        versionCode = 10000
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../sign/user.keystore")
            storePassword = "capture001"
            keyAlias = "faceCapture"
            keyPassword = "faceCapture"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig =  signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Face mesh features
    implementation(libs.face.mesh.detection)
    // Face features
    implementation(libs.face.detection)

    implementation(libs.ml.camera)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    implementation(libs.guava)

    implementation(project(":framework"))
    implementation(files("../libs/Live2DCubismCore.aar"))
}