import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.ben.manes.versions)
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")


configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
    }
}

dependencies {
    implementation(libs.spring.boot.configuration.processor)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.webflux)
    implementation(libs.jakarta.validation.api)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.springdoc.openapi.webflux)
    implementation(project(":loki-client"))
    compileOnly(libs.lombok)
    compileOnly(libs.mapstruct)

    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)

    // Зависимости для тестирования
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.spring.boot.starter.test)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
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