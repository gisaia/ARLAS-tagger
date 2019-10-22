#####################
# COMPILATION STAGE #
#####################
FROM maven:3.5-jdk-8-alpine as build
WORKDIR /opt/build

# selectively add the POM file
ADD pom.xml /opt/build/
# get all the downloads out of the way
RUN mvn verify clean --fail-never

# build all project
COPY . /opt/build/
RUN mvn install \
    && mv /opt/build/arlas-tagger/target/arlas-tagger-*.jar /opt/build/arlas-tagger.jar

###################
# PACKAGING STAGE #
###################
FROM openjdk:8-jre-alpine
WORKDIR /opt/app

# install script dependencies
RUN apk add --update bash netcat-openbsd curl && rm -rf /var/cache/apk/*

# application placed into /opt/app
WORKDIR /opt/app
COPY --from=build /opt/build/arlas-tagger.jar /opt/app/
COPY --from=build /opt/build/conf/configuration.yaml /opt/app/
COPY --from=build /opt/build/docker/docker-resources/wait-for-elasticsearch.sh /opt/app/
COPY --from=build /opt/build/docker/docker-resources/wait-for-tagger-deps.sh /opt/app/
COPY --from=build /opt/build/docker/docker-resources/start.sh /opt/app/
EXPOSE 9998

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl http://localhost:9998/admin/healthcheck | grep -v "\"healthy\":false" || exit 1

ENTRYPOINT ["bash", "-c"]
CMD ["/opt/app/start.sh"]