FROM amazoncorretto:17-alpine

WORKDIR /app

COPY service/target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
