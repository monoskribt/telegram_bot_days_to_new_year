FROM openjdk:17-jdk-slim

WORKDIR /usr/java

COPY . /usr/java

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/telegram_bot_days_to_new_year-0.0.1-SNAPSHOT.jar"]
