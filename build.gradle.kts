import org.gradle.kotlin.dsl.implementation

plugins {
    java
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
}
val springCloudAzureVersion by extra("6.1.0")

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "ms-password-recovery-service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.azure.spring:spring-cloud-azure-starter-keyvault-secrets")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.10")
    implementation("org.springframework.kafka:spring-kafka")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    runtimeOnly("io.r2dbc:r2dbc-mssql:1.0.0.RELEASE")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
dependencyManagement {
    imports {
        mavenBom("com.azure.spring:spring-cloud-azure-dependencies:$springCloudAzureVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
