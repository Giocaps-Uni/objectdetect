// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
}

allprojects {
    repositories {
        // If you're using a version of Gradle lower than 4.1, you must instead use:
        mavenCentral()
        maven {
            setUrl("https://maven.google.com")
        }
        // An alternative URL is 'https://dl.google.com/dl/android/maven2/'

    }
}

buildscript {
    repositories {
        // If you're using a version of Gradle lower than 4.1, you must instead use:
        mavenCentral()
        maven {
            setUrl("https://maven.google.com")
        }
        // An alternative URL is 'https://dl.google.com/dl/android/maven2/'

    }
}