// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.2.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
