plugins {
    java
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "edu.booking"
version = "0.0.1-SNAPSHOT"
description = "Система бронирования номеров в отеле"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.postgresql:postgresql:42.7.8")
	implementation("org.liquibase:liquibase-core:4.33.0")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.4")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	compileOnly("org.projectlombok:lombok:1.18.34")
	annotationProcessor("org.projectlombok:lombok:1.18.34")
	testCompileOnly("org.projectlombok:lombok:1.18.34")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.34")
	implementation("org.springframework.kafka:spring-kafka")
}


tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("-Xshare:off")
}
