FROM eclipse-temurin:17-jdk-alpine
COPY build/libs/ktor-letter-pet-0.0.1.jar /app/ktor-letter-pet-0.0.1.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ktor-letter-pet-0.0.1.jar"]
