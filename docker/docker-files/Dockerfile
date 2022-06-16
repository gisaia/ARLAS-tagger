#####################
# COMPILATION STAGE #
#####################
FROM maven:3.8.5-openjdk-17 as build
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
FROM gisaia/arlas-openjdk:17-distroless

# application placed into /opt/app
WORKDIR /opt/app
COPY --from=build /opt/build/arlas-tagger.jar /opt/app/
COPY --from=build /opt/build/conf/configuration.yaml /opt/app/
EXPOSE 9998

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl http://localhost:9998/admin/healthcheck | grep -v "\"healthy\":false" || exit 1

ENV JDK_JAVA_OPTIONS="-Xmx512m -XX:+ExitOnOutOfMemoryError"
CMD ["arlas-tagger.jar", "server", "/opt/app/configuration.yaml"]
