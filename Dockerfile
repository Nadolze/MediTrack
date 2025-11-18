FROM openjdk:17-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Xmx500m","-XX:ActiveProcessorCount=1","-jar","/app.jar"]
