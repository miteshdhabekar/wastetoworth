# Use a lightweight OpenJDK base image
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /project

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use an official OpenJDK image to run the application
FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /project

# Copy the built JAR file from the build stage
COPY --from=build /project/target/project-0.0.1-SNAPSHOT.jar .

# Expose the port your Spring Boot application listens on (default is 8080)
EXPOSE 8080

# Define the command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "/project/project-0.0.1-SNAPSHOT.jar"]
