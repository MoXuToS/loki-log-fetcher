plugins {
    id("java")
}

group = "ru.loki.fetcher"
version = "3.0.0-migrate-gradlew"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(1.8))
    }
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
