import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    `java-library`
    alias(libs.plugins.ben.manes.versions)
}

group = "org.moxutos.airport"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")


configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${libs.versions.spring.cloud.get()}")
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
    }
}

dependencies {
    implementation(libs.spring.boot.configuration.processor)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.cloud.starter.openfeign)
    implementation(libs.jakarta.validation.api)
    implementation(libs.jackson.databind)
    implementation(libs.spring.cloud.starter.loadbalancer)
    implementation(libs.jackson.datatype.jsr310)
    compileOnly(libs.lombok)
    compileOnly(libs.mapstruct)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.spring.boot.starter.test)

    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}