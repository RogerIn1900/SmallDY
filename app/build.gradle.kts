import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.internal.types.error.ErrorModuleDescriptor.platform

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.smalldy"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.smalldy"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    
    packaging {
        resources {
            // 排除冲突的 XML 解析库和重复的类文件
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "**/xpp3-*.jar"
            excludes += "**/xpp3/**"
        }
    }
}

// 配置依赖解析策略，排除冲突的库
configurations.all {
    exclude(group = "xpp3", module = "xpp3")
    resolutionStrategy {
        // 强制使用 xmlpull 而不是 xpp3
        force("xmlpull:xmlpull:1.1.3.1")
    }
}

dependencies {
    // Media3 ExoPlayer - 使用最新稳定版本
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    implementation("androidx.media3:media3-common:1.8.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.8.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.8.0")
    implementation("androidx.media3:media3-datasource:1.8.0")

    // Compose BOM - 统一版本管理 (2025.08.00)
    implementation(platform(libs.androidx.compose.bom))
    
    // Compose 核心库
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.foundation.layout)
    // Pull-to-refresh（与 Compose 1.10.x 对齐）

    // Material Icons (通过 BOM 管理版本)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity & Lifecycle
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.4") // 使用更高版本

    // Navigation Compose - 使用 libs.versions.toml 中的版本 (2.9.6)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.safe.args.generator)

    // Window Size Class
    implementation(libs.androidx.compose.material3.window.size.class1)

    // Hilt - 使用最新版本（与根 build.gradle.kts 中的版本一致）
    implementation("com.google.dagger:hilt-android:2.57.1")
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.runtime)
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")

    // 图片加载 - Coil
    implementation("io.coil-kt:coil-compose:2.7.0") // 更新到更高版本

    // Core KTX
    implementation(libs.androidx.core.ktx)

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Debug Tools
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}