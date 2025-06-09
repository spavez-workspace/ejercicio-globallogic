plugins {
	java
	id("org.springframework.boot") version "2.5.14"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.globallogic"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
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
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")	
    implementation("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.hibernate.validator:hibernate-validator:6.2.5.Final")
	implementation("javax.el:javax.el-api:3.0.0")
	runtimeOnly("org.glassfish:javax.el:3.0.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")	
	testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")
    testImplementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("org.springdoc:springdoc-openapi-ui:1.7.0")
    implementation("org.springdoc:springdoc-openapi-data-rest:1.7.0")    
    implementation("io.swagger.core.v3:swagger-annotations:2.2.15")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
