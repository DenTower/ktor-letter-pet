FROM openjdk:11
COPY build/libs/ktor-letter-pet-0.0.1.jar /app/ktor-letter-pet-0.0.1.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/ktor-letter-pet-0.0.1.jar"]
