import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.xiaoming"
version = "1.0-SNAPSHOT"
val appVersion = "1.0.0"
repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.android.tools.ddms:ddmlib:31.3.1")
                implementation("ch.qos.logback:logback-classic:1.2.3")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        nativeDistributions{
            includeAllModules = true
        }
        buildTypes.release.proguard {
            isEnabled.set(false)
            obfuscate.set(false)
        }
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "AdbTool"
            packageVersion = appVersion
            windows {
                // a version for all Windows distributables
                packageVersion = appVersion
                // a version only for the msi package
                msiPackageVersion = appVersion
                // a version only for the exe package
                exePackageVersion = appVersion
                iconFile.set(project.file("launcher/logo.ico"))
                menu = true
                shortcut = true
            }
            macOS {
                // a version for all macOS distributables
                packageVersion = appVersion
                // a version only for the dmg package
                dmgPackageVersion = appVersion
                // a version only for the pkg package
                pkgPackageVersion = appVersion
                // 显示在菜单栏、“关于”菜单项、停靠栏等中的应用程序名称
                dockName = "AdbTool"
                // a build version for all macOS distributables
                packageBuildVersion = appVersion
                // a build version only for the dmg package
                dmgPackageBuildVersion = appVersion
                // a build version only for the pkg package
                pkgPackageBuildVersion = appVersion
                // 设置图标
                iconFile.set(project.file("launcher/logo.icns"))
            }
        }
    }
}
