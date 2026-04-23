import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.kotlin.dsl.configure

plugins {
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    `java-library`
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
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.http.client)
    implementation(libs.webflux)
    implementation(libs.reactor.netty.http)
    implementation(libs.jackson.databind)
    implementation(libs.jakarta.validation.api)
    compileOnly(libs.lombok)
    compileOnly(libs.mapstruct)

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