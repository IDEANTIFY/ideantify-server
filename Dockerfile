# Azul Zulu OpenJDK 21 (JRE 포함)
FROM azul/zulu-openjdk:21

WORKDIR /app

# jar 파일 복사
COPY build/libs/*.jar app.jar

# 포트 열기
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]