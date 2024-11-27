plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

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
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://mvn.0110.be/releases") }
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.3")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")



	// open ai
	implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT"))
	implementation("org.springframework.ai:spring-ai-openai")
	implementation("com.theokanning.openai-gpt3-java:service:0.11.0") // OpenAI Java 클라이언트

	// web socket
	implementation("org.springframework:spring-websocket:6.1.12")

	implementation("org.bytedeco:javacv:1.3.1")

	implementation("be.tarsos.dsp:core:2.5")
	implementation("be.tarsos.dsp:jvm:2.5")

	//jwt
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
	implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")



	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	annotationProcessor("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("com.h2database:h2")

}

tasks.withType<Test> {
	useJUnitPlatform()
}
