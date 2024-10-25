FROM sbtscala/scala-sbt:eclipse-temurin-alpine-22_36_1.10.3_3.5.2

WORKDIR /app

RUN apk add --no-cache nodejs npm

COPY build.sbt /app/
COPY project /app/project

COPY . /app

RUN sbt compile

EXPOSE 8080

CMD ["sbt", "run"]