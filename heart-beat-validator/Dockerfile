FROM openjdk:11.0.6-jre-slim

EXPOSE 8280
WORKDIR /opt
COPY target/heart-beat-validator.jar /opt
ENTRYPOINT ["java", "-jar", "/opt/heart-beat-validator.jar"]
