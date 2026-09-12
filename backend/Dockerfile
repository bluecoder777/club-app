FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests



FROM tomcat:10.1-jdk21-temurin
LABEL author="JEFRIN ELDHOS JOY"

COPY --from=build /app/target/club-api.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

