plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "org.kaustav.majorproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.kaustav.majorproject"
        minSdk =26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.5.0")
//    implementation ("org.apache.poi:poi:5.2.5")
//    implementation ("org.apache.poi:poi-ooxml:5.2.5")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation ("org.apache.poi:poi:5.2.4")
    implementation ("org.apache.poi:poi-ooxml:5.2.4")
    implementation ("org.apache.xmlbeans:xmlbeans:5.1.1")


    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation ("com.firebaseui:firebase-ui-database:8.0.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}