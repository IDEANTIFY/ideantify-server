# 멀티아키 지원되는 Temurin JRE 21 사용
FROM eclipse-temurin:21-jre

WORKDIR /app

# jar 파일 복사
COPY build/libs/*.jar app.jar

# 포트 열기
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]