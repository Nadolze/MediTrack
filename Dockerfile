# Basis-Image mit Java 17
FROM openjdk:17-jdk-slim

# Maven-Build Output (Jar) kopieren
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Ressourcen-Limits in der JVM: max. 500MB RAM, nur 1 CPU
ENTRYPOINT ["java","-Xmx500m","-XX:ActiveProcessorCount=1","-jar","/app.jar"]
