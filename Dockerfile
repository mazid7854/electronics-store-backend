# Use a lightweight Java 21 base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file to the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 9090

# Set the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
