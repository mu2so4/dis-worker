FROM openjdk:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
MAINTAINER ru.nsu.ccfit.muratov
ENTRYPOINT ["java","-jar","/app.jar"]