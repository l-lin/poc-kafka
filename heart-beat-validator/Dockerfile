FROM openjdk:11.0.6-jre-slim

EXPOSE 8083
EXPOSE 5006
WORKDIR /opt
COPY target/heart-beat-validator.jar /opt
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006", "-jar", "/opt/heart-beat-validator.jar"]
