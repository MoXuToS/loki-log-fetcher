plugins {
    id("java")
    alias(libs.plugins.dependency.analysis) apply false
}

group = "moxutos.loki.fetcher"
version = "3.0.0-migrate-gradlew"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
