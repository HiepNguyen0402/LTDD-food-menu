plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")


}

android {
    namespace = "com.example.halidao"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.halidao"
        minSdk = 26
        targetSdk = 35
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

}

dependencies {
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation ("com.sun.mail:android-activation:1.6.2")
    implementation ("com.sun.mail:android-mail:1.6.2")
    implementation("org.apache.poi:poi-ooxml-lite:5.2.3")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("org.apache.poi:poi:5.2.3")
    implementation ("org.apache.poi:poi-ooxml:5.2.3")
    implementation(libs.glide)
    kapt(libs.glide.compiler)
    implementation ("com.github.bumptech.glide:glide:4.15.1") // Hoặc phiên bản mới nhất
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    implementation ("com.google.android.material:material:1.9.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}