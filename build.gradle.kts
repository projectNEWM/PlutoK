import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

plugins {
    id("com.github.ben-manes.versions") version Versions.versionsPlugin
    kotlin("jvm") version Versions.kotlin
    id("maven-publish")
    id("signing")
    id("org.hibernate.build.maven-repo-auth") version Versions.mavenRepoAuthPlugin apply false
}

allprojects {
    group = "io.newm.plutok"
    version = "0.0.1-SNAPSHOT"

    fun isNonStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<DependencyUpdatesTask> {
        // Example 1: reject all non stable versions
        rejectVersionIf {
            isNonStable(candidate.version)
        }

        // Example 2: disallow release candidates as upgradable versions from stable versions
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }

        // Example 3: using the full syntax
        resolutionStrategy {
            componentSelection {
                all {
                    if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                        reject("Release candidate")
                    }
                }
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain>().configureEach {
        val service = project.extensions.getByType<JavaToolchainService>()
        val customLauncher = service.launcherFor {
            this.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
        }

        this.kotlinJavaToolchain.toolchain.use(customLauncher)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        maxHeapSize = "8192m"
    }
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
