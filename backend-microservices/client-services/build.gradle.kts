import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.spring") version "1.3.61"
}

val snippetsDir by extra { file("build/generated-snippets") }

group = "com.utsman.kemana"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "Hoxton.SR1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("junit:junit:4.12")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client") {
        exclude(group = "com.fasterxml.jackson.dataformat", module = "jackson-dataformat-xml")
    }
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server") {
        exclude(group = "com.fasterxml.jackson.dataformat", module = "jackson-dataformat-xml")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    copy {
        from("build/libs/backend-0.0.1.jar")
        into("../eureka-server")
    }

    "asciidoctor"(org.asciidoctor.gradle.AsciidoctorTask::class) {
        sourceDir = file("$projectDir/docs/ascii")
        outputDir = file("docs")
        attributes(
                mapOf(
                        "snippets" to snippetsDir
                )
        )
    }
}