FROM amazoncorretto:17
LABEL authors="dski2335"
COPY build/libs/*.jar app.jar
# 자바를 실행하는 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]