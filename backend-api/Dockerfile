# 1. Base image
FROM eclipse-temurin:21-jdk-alpine

# 2. Thiết lập thư mục làm việc
WORKDIR /app

# 3. Copy file JAR (pattern bất kỳ trong target/)
COPY target/*.jar app.jar

# 4. Expose port (Spring Boot profile develop:8081, staging:8082)
EXPOSE 8081 8082

# 5. Chạy Spring Boot với profile truyền từ environment
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
