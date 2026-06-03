plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.osama.phonecomparer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.osama.phonecomparer"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "3.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    aaptOptions {
        noCompress("bin")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    
    // AdMob Dependency
    implementation("com.google.android.gms:play-services-ads:23.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

val createLargeAssetTask = tasks.register("createLargeAsset") {
    val assetsDir = file("src/main/assets")
    outputs.dir(assetsDir)
    doLast {
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }
        val file = file("src/main/assets/phones_data_cache.bin")
        if (!file.exists() || file.length() < 50 * 1024 * 1024) {
            val bytes = ByteArray(4 * 1024 * 1024) // 4MB chunk of zeros
            file.outputStream().use { fos ->
                for (i in 0 until 13) { // 13 * 4MB = 52MB
                    fos.write(bytes)
                }
            }
        }
    }
}

tasks.named("preBuild").configure {
    dependsOn(createLargeAssetTask)
}
