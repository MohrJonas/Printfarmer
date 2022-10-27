import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose")
}

group = "mohr.jonas"
version = "0.0.1-PreAlpha"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("io.insert-koin:koin-core-jvm:3.2.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation("org.amshove.kluent:kluent:1.69")
                implementation("io.ktor:ktor-client-core:2.0.3")
                implementation("io.ktor:ktor-client-apache:2.0.3")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")
                implementation("org.apache.commons:commons-lang3:3.12.0")
                implementation("commons-validator:commons-validator:1.7")
                implementation("org.jetbrains.compose.ui:ui-graphics-desktop:1.1.0")
                implementation("org.jetbrains.compose.ui:ui-geometry-desktop:1.1.0")
                implementation("org.jetbrains.compose.foundation:foundation-desktop:1.1.0")
                implementation("com.godaddy.android.colorpicker:compose-color-picker-jvm:0.5.1")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "mohr.jonas.printfarmer.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.AppImage)
            packageName = "printfarmer"
            packageVersion = "1.0.0"
        }
    }
}
