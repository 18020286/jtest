FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/auth-service-1.0.0.jar
COPY ${JAR_FILE} auth-service-1.0.0.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/auth-service-1.0.0.jar"]