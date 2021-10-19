FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=route-impl/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-jar","/app.jar"]