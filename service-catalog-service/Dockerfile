FROM openjdk:17-jdk-alpine
VOLUME /tmp
COPY target/service-catalog-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
