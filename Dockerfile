FROM gradle:8.12-jdk21-alpine AS build
WORKDIR /home/gradle/src
COPY . .
# 나중에 테스트까지 포함하여 빌드하도록 수정해야 함.
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
