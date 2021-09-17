FROM openjdk:16-jdk-alpine3.13
EXPOSE 8080
ADD target/task-manager-backend.jar task-manager-backend.jar
ENTRYPOINT ["java", "-jar", "task-manager-backend.jar"]