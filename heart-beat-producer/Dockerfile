FROM openjdk:11.0.6-jre-slim

EXPOSE 8080
EXPOSE 5005
WORKDIR /opt
COPY target/heart-beat-producer.jar /opt
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/opt/heart-beat-producer.jar"]
