plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
}

android {
    namespace = "com.ugrcaan.qriosity"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.ugrcaan.qriosity"
        minSdk = 29
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

}

dependencies {
    val coreKtxVersion = "1.10.1"
    val appcompatVersion = "1.6.1"
    val materialVersion = "1.9.0"
    val constraintLayoutVersion = "2.1.4"
    val lifecycleVersion = "2.6.1"
    val navigationVersion = "2.6.0"
    val junitVersion = "4.13.2"
    //val espressoVersion = "3.5.1"
    val kotlinVersion = "1.8.21"
    val coroutinesVersion = "1.6.4"
    val roomVersion = "2.5.2"
    val lottieVersion = "3.4.0"

    implementation("androidx.core:core-ktx:$coreKtxVersion")
    implementation("androidx.appcompat:appcompat:$appcompatVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    //testImplementation("junit:junit:$junitVersion")
    //androidTestİmplementation("androidx.test.ext:junit:1.1.5")
    //androidTestİmplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Room components
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    //androidTestİmplementation("androidx.room:room-testing:$roomVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    //Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    //QR Library
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    //implementation "androidx.multidex:multidex:2.0.1"

    implementation ("com.airbnb.android:lottie:$lottieVersion")


}