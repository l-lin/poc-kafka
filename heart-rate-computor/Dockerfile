FROM openjdk:11.0.6-jre-slim

EXPOSE 8380
WORKDIR /opt
COPY target/heart-rate-computor.jar /opt
ENTRYPOINT ["java", "-jar", "/opt/heart-rate-computor.jar"]
