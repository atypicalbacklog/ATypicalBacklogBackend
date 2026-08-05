plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    
}

group = "at.atypicalbacklog"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
        resources {
            srcDirs("src/main/resources")
        }
    }
    test {
        java {
            srcDirs("src/test/java")
        }
        resources {
            srcDirs("src/test/resources")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Core
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Persistence
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")

    // CSV parsing
    implementation("org.apache.commons:commons-csv:1.14.1")

    // Google Calendar API (sync time bookings)
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20251207-2.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.25.0")

    // Local comment moderation (profanity dictionary + optional ONNX toxicity)
    implementation("com.microsoft.onnxruntime:onnxruntime:1.19.2")
    implementation("ai.djl:api:0.30.0")
    implementation("ai.djl.huggingface:tokenizers:0.30.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
