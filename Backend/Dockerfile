# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:17-al2023-jdk

# Set the working directory in the container
WORKDIR /usr/app

# Copy the compiled JAR file from Gradle build directory
COPY build/libs/app.jar app.jar

# Expose the port on which your Spring Boot application will run
EXPOSE 8080

# Define the command to run your Spring Boot application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
