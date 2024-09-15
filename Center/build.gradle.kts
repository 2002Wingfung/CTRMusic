import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
//    kotlin("kapt") version "2.0.0"
}

android {
    namespace = "com.example.center"
    compileSdk = 34

    defaultConfig {
        minSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled=false
            isShrinkResources=false

            proguardFiles(getDefaultProguardFile("proguard-android.txt"),"proguard-rules.pro")
        }
    }
    buildFeatures{
        dataBinding=true
        aidl=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
//    kapt("groupId:artifactId:version")
    implementation("androidx.media3:media3-ui:1.4.0")
    implementation("androidx.media3:media3-common:1.4.0")
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation(project(":JetpackMVVM"))
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.guolindev.permissionx:permissionx:1.7.1")
    implementation("com.jakewharton:disklrucache:2.0.2")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.room:room-runtime:2.5.1")
    implementation("androidx.room:room-ktx:2.5.1")
    implementation("io.github.cymchad:BaseRecyclerViewAdapterHelper4:4.1.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("io.github.youth5201314:banner:2.2.3")
    implementation("io.coil-kt:coil:2.6.0")
//    implementation("com.github.githubwing:ByeBurger:1.2.3")
//    implementation("com.android.support:design:25.0.0")
    implementation(project(":Utils"))
    implementation(project(":Player"))

}