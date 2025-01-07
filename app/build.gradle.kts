import com.android.builder.files.classpathToRelativeFileSet

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.diaryapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.diaryapp"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    implementation(libs.fragment)
    testImplementation(libs.junit)

//    implementation(libs.play.services)
    implementation(libs.play.services.auth)

//    implementation(com.google.firebase:firebase-storage)
//    implementation platform(libs.firebase.fi)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.storage)
//    implementation("com.google.firebase:firebase-database")
//    implementation 'com.google.firebase:firebase-auth'

//    implementation("com.google.android.gms:play-services-auth:20.7.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
//    implementation(com.google.android.gms:play-)
//    implementation(com.google.android.gms:play-services-auth:20.7.0)
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    implementation ("androidx.room:room-runtime:2.5.0")
    implementation ("com.google.android.material:material:1.2.0-alpha04")

    kapt ("androidx.room:room-compiler:2.5.0")

    annotationProcessor(libs.room.compiler) // 必須包含此依賴

}

fun kapt(s: String) {

}
