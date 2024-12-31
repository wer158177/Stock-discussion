# Java 기반 애플리케이션 예제
FROM openjdk:21-jdk-slim

# 애플리케이션 작업 디렉토리
WORKDIR /app

# JAR 파일 복사
COPY build/libs/User-Service-0.0.1-SNAPSHOT.jar app.jar

# application.yml 파일 복사 (경로를 맞게 수정)
COPY src/main/resources/application.yml /app/application.yml


ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

ENV JWT_SECRET_KEY=${JWT_SECRET_KEY}
ENV EUREKA_DEFAULT_ZONE=${EUREKA_DEFAULT_ZONE}
# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
