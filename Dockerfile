# Beispiel mit Microsofts OpenJDK
FROM mcr.microsoft.com/openjdk/jdk:17-ubuntu

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Xmx500m","-XX:ActiveProcessorCount=1","-jar","/app.jar"]