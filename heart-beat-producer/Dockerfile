FROM openjdk:11.0.6-jre-slim

WORKDIR /opt
COPY target/heart-beat-producer.jar /opt
ENTRYPOINT ["java", "-jar", "/opt/heart-beat-producer.jar"]