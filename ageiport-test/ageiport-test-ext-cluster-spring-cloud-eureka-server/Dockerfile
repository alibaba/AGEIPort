FROM openjdk:17-jdk-alpine

VOLUME /tmp

ADD **/*.jar app.jar

RUN sh -c 'touch /app.jar'

CMD exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=production -jar /app.jar