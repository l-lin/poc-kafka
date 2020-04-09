FROM openjdk:11.0.6-jre-slim

EXPOSE 8480
WORKDIR /opt
COPY target/heart-rate-consumer.jar /opt
ENTRYPOINT ["java", "-jar", "/opt/heart-rate-consumer.jar"]
