plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.jetpackmvvm"
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
    }
    buildFeatures{
        dataBinding=true
        viewBinding=true
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
//    implementation(libs.androidx.lifecycle.process)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    api(libs.androidx.navigation.fragment.ktx.v235)
    api(libs.androidx.navigation.ui.ktx)
    api(libs.retrofit.url.manager)
    api(libs.roundedimageview)
    api(libs.androidx.lifecycle.viewmodel.ktx)
//    api(libs.jetpackmvvm)
    api(libs.unpeek.livedata)
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0")
    api("androidx.lifecycle:lifecycle-common-java8:2.2.0")
    api("androidx.lifecycle:lifecycle-extensions:2.2.0")

}