plugins {
    id("java")
}

group = "ru.loki.fetcher"
version = "3.0.0-migrate-java-21"

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
