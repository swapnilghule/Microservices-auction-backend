FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy built jar into container
COPY target/*.jar app.jar

# Run the Spring Boot app
ENTRYPOINT ["java","-jar","/app/app.jar"]