import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    js(IR) {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)


            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.play.services.maps)
        }

        val jsMain by getting {
            dependencies {
            }
        }
    }
}

android {
    namespace = "tz.mtanav"
    compileSdk = 34

    defaultConfig {
        applicationId = "tz.mtanav"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Alias for wasmJsBrowserDevelopmentRun -> jsBrowserDevelopmentRun
tasks.register("wasmJsBrowserDevelopmentRun") {
    group = "kotlin browser"
    description = "Alias to launch development webpack server"
    dependsOn("jsBrowserDevelopmentRun")
}

// Automatically extract the matching skiko.js and skiko.wasm from the project's
// Skiko dependency into jsMain/resources so runtime WASM bindings are 100% in sync.
val unpackSkikoTask = tasks.register("unpackSkiko") {
    doLast {
        val skikoJar = configurations.findByName("jsCompileClasspath")?.files?.find {
            it.name.contains("skiko") && (it.name.endsWith(".jar") || it.name.endsWith(".klib"))
        }
        if (skikoJar != null) {
            copy {
                from(zipTree(skikoJar))
                into(file("src/jsMain/resources"))
                include("skiko.js", "skiko.wasm")
                include("**/skiko.js", "**/skiko.wasm")
                eachFile {
                    path = name
                }
            }
        }
    }
}

tasks.matching { it.name.contains("ProcessResources") || it.name.contains("Webpack") }.configureEach {
    dependsOn(unpackSkikoTask)
}
