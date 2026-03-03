# 1단계: 빌드 환경 (Gradle & Java 21)
# 빌드 시에만 필요한 툴들을 포함하고, jar 파일을 뽑아냅니다.
FROM gradle:8.12-jdk21-alpine AS build
WORKDIR /home/gradle/src
COPY . .
# 테스트를 제외하고 빌드하여 시간을 대폭 단축합니다.
RUN gradle clean build -x test

# 2단계: 실행 환경 (JRE 21)
# 런타임에 필요한 가벼운 JRE 환경만 구성합니다.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 1단계에서 빌드된 jar 파일을 복사합니다.
# 'build/libs/' 아래의 *-SNAPSHOT.jar를 찾습니다.
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# 컨테이너 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
