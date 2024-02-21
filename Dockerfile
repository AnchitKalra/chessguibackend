FROM eclipse-temurin:17
RUN mkdir /app
COPY /target/hello-world-1.0-SNAPSHOT.jar /app
EXPOSE 8080
CMD ["java", "-jar", "/app/hello-world-1.0-SNAPSHOT.jar"]
