import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.spring.dependency.management)
    `java-library`
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.dependency.analysis)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

extra["snippetsDir"] = file("build/generated-snippets")

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${libs.versions.spring.boot.get()}")
    }
}

dependencies {
    api(libs.webflux)
    api(libs.webclient)
    api(libs.micrometer.observation)
    api(libs.jackson.core)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    api(libs.reactor.core)
    api(libs.spring.boot)
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.beans)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.spring.web)
    implementation(libs.reactor.netty.http)
    implementation(libs.netty.handler)
    implementation(libs.netty.transport)
    implementation(libs.reactor.netty.core)
    implementation(libs.jakarta.validation.api)
    implementation(libs.commons.collections4)
    compileOnly(libs.jetbrains.annotation)
    compileOnly(libs.lombok)
    compileOnly(libs.mapstruct)
    annotationProcessor(libs.lombok)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("build") {
    dependsOn("projectHealth")
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}