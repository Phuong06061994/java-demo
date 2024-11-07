FROM openjdk:17-jdk-slim AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

# Copy the necessary files
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Resolve dependencies
RUN ./mvnw dependency:resolve

# Copy source code
COPY src src

# Package the application
RUN ./mvnw package

FROM openjdk:17-jdk-slim
WORKDIR /demo

# Copy the JAR file from the build stage
COPY --from=build target/*.jar demo.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "demo.jar"]
