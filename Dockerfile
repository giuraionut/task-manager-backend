FROM openjdk:16-jdk-alpine3.13
EXPOSE 8080
ADD target/taskmanager-backend.jar taskmanager-backend.jar
ENTRYPOINT ["java", "-jar", "taskmanager-backend.jar"]