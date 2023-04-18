FROM openjdk:17-jdk-alpine

RUN apk update \
    && apk upgrade \
    && apk add --no-cache curl \
    && apk add --no-cache freetype \
    && apk add --no-cache fontconfig \
    && apk add --no-cache ttf-dejavu


VOLUME /tmp

ADD **/*.jar app.jar

RUN sh -c 'touch /app.jar'

CMD exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=production -jar /app.jar