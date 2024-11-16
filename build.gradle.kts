buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("com.google.gms:google-services:4.3.15")
        // Add other classpath dependencies here
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        // Remove any repository declarations here
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
