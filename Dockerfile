FROM java:8-jdk-alpine
COPY target/report-1.jar report-1.jar
ENTRYPOINT ["java", "-jar", "/report-1.jar"]
EXPOSE 8080