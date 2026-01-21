import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import kotlin.apply

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.allopen") version "2.2.21"

	id("org.springframework.boot") version "3.5.8"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.polarbookshop"
version = "0.0.1-SNAPSHOT"
description = "Functionality for dispatching orders"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.github.oshai:kotlin-logging-jvm:${property("kotlin-logging-jvm.version")}")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("io.kotest:kotest-assertions-core-jvm:${property("kotest.version")}")
	testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:rabbitmq")
	testImplementation("org.awaitility:awaitility-kotlin:${property("awaitility-kotlin.version")}")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("spring-cloud.version")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named<BootBuildImage>("bootBuildImage") {
	imageName.set(project.name)
	environment.set(
		mapOf(
			"BP_JVM_VERSION" to "21.*"
		)
	)

	docker.apply {
		publishRegistry.apply {
			username.set(project.findProperty("registryUsername") as String?)
			password.set(project.findProperty("registryToken") as String?)
			url.set(project.findProperty("registryUrl") as String?)
		}
	}
}
