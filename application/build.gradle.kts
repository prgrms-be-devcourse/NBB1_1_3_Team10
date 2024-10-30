plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("plugin.jpa") version "1.9.25"
}
val springCloudGcpVersion by extra("5.7.0")

group = "core"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring boot
    implementation ("org.springframework.boot:spring-boot-starter-web")

    // MyBatis
    implementation ("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    implementation ("org.mybatis.dynamic-sql:mybatis-dynamic-sql:1.5.2")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring data jpa
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")


    // Swagger
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // DTO validation
    implementation ("org.springframework.boot:spring-boot-starter-validation")

    // Google Cloud Storage
    implementation ("com.google.cloud:spring-cloud-gcp-starter")
    implementation ("com.google.cloud:spring-cloud-gcp-starter-storage")

    // Caching
    implementation ("org.springframework.boot:spring-boot-starter-cache")
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")

    // JWT tokens
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
    implementation("io.jsonwebtoken:jjwt-gson:0.12.5")

    // WebFlux
    implementation ("org.springframework.boot:spring-boot-starter-webflux")

    // Json
    implementation ("org.json:json:20211205")

    // redis
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")
    implementation ("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // spring security
    implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation ("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation ("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    compileOnly ("org.projectlombok:lombok")
    runtimeOnly ("com.mysql:mysql-connector-j")
    annotationProcessor ("org.projectlombok:lombok")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
    testImplementation ("org.mybatis.spring.boot:mybatis-spring-boot-starter-test:3.0.3")
    testRuntimeOnly ("org.junit.platform:junit-platform-launcher")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib")

    compileOnly("org.projectlombok:lombok:1.18.28")

    testCompileOnly("org.projectlombok:lombok:1.18.28")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
dependencyManagement {
    imports {
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:$springCloudGcpVersion")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
