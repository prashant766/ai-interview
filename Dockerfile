FROM maven:3.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java","-jar","target/AI-interview-0.0.1-SNAPSHOT.jar"]