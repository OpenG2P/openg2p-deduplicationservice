FROM openjdk:8-jdk-alpine

MAINTAINER saltonmassally@gmail.com

RUN apk add --no-cache bash

EXPOSE 8080
RUN mkdir /app
WORKDIR /app
COPY build/libs/searchservice-0.0.1-SNAPSHOT.jar ./app.jar
COPY scripts/wait-for-it.sh .
COPY scripts/docker-run.sh ./run.sh
RUN chmod +x ./run.sh
RUN chmod +x ./wait-for-it.sh
ENTRYPOINT ["./run.sh"]
