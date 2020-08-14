FROM maven:3.5-jdk-8-alpine as build 

RUN apk add --no-cache git

WORKDIR /app

# Use build-time arguments to invalidate cache 
# (e.g. --build-arg CACHE_DATE=$(date +%Y-%m-%d:%H:%M:%S))
ARG CACHE_DATE=not_a_date

# for the moment we still use the wacodis fork, but only until the PR
# is merged (https://github.com/52North/javaPS/pull/52)
# then --> 52North/javaps:develop branch should be
RUN git clone https://github.com/WaCoDiS/javaPS.git javaps \
 	&& git -C ./javaps checkout wacodis-developments

RUN mvn -f ./javaps/pom.xml clean install -DskipTests -pl !webapp

#clone and build snap dependencies 
# (optional, if artifacts are not available in the snap repository, it might be necessary to build complete projects instead of selected modules)
#snap core
#RUN git clone https://github.com/senbox-org/snap-engine.git snap-engine \
# 	&& git -C ./snap-engine  checkout master
#RUN mvn -f ./snap-engine/pom.xml clean install -DskipTests -pl snap-runtime,snap-core,snap-geotiff,snap-ndvi,snap-raster,snap-bigtiff,lib-openjpeg

#sentinel 1 toolbox
#RUN git clone https://github.com/senbox-org/s1tbx.git s1tbx \
#	&& git -C ./s1tbx  checkout master
#RUN mvn -f ./s1tbx/pom.xml clean install -DskipTests -pl s1tbx-io,s1tbx-commons,s1tbx-op-sar-processing,s1tbx-op-utilities,s1tbx-op-calibration

#sentinel 2 toolbox
#RUN git clone https://github.com/senbox-org/s2tbx.git s2tbx \
# 	&& git -C ./s2tbx  checkout master
#RUN mvn -f ./s2tbx/pom.xml clean install -DskipTests -pl s2tbx-s2msi-reader,s2tbx-s2msi-resampler,s2tbx-radiometric-indices


COPY ./pom.xml ./wacodis-backend/pom.xml
# Cache dependencies as long as the POM changes
RUN mvn -f ./wacodis-backend/pom.xml dependency:go-offline

COPY ./src ./wacodis-backend/src
# RUN git clone https://github.com/WaCoDiS/javaps-wacodis-backend.git wacodis-backend \
# 	&& git -C ./wacodis-backend checkout master

RUN mvn -f ./wacodis-backend/pom.xml clean install -DskipTests
COPY ./docker/custom-webapp-pom.xml ./javaps/webapp/pom.xml

# skip enforcer for the time being (SNAP is hard to fix)
RUN mvn -f ./javaps/webapp/pom.xml package -DskipTests -Denforcer.skip=true

RUN ls ./javaps/webapp/target
RUN bash -c 'find ./javaps/webapp/target -maxdepth 1 -size +1048576c | grep ".war" | xargs -I{} mv {} ./javaps/webapp/target/wacodis-javaps.war'
RUN ls ./javaps/webapp/target

FROM tomcat:9-jre8-slim
ARG WAR_FILE=wacodis-javaps.war
ARG TARGET_DIR=wacodis-javaps/

WORKDIR /app
COPY --from=build /app/javaps/webapp/target/${WAR_FILE} $CATALINA_HOME/webapps/

RUN mkdir /tmp/workdir \
	&& mkdir /tmp/tools \
	&& mkdir /tmp/gpf \
	&& mkdir -p /tmp/wps/outputs

# extract WAR file
RUN cd ${CATALINA_HOME}/webapps/ && \
    unzip ${WAR_FILE} -d ${TARGET_DIR} && \
    rm 	${WAR_FILE}
# overwrite log config
COPY ./docker/log4j2_docker.xml ${CATALINA_HOME}/webapps/${TARGET_DIR}WEB-INF/classes/log4j2.xml

# install jq for faroe environemnt variable injection
RUN apt-get update \
 && apt-get install -y --no-install-recommends jq wget \
 && rm -rf /var/lib/apt/lists/* \
 && wget -q -P /usr/local/bin https://raw.githubusercontent.com/52North/arctic-sea/master/etc/faroe-entrypoint.sh \
 && chmod +x /usr/local/bin/faroe-entrypoint.sh

ENV FAROE_CONFIGURATION ${CATALINA_HOME}/webapps/${TARGET_DIR}WEB-INF/config/configuration.json

ENV JAVA_OPTS="-Xmx4g -Xms2g"

CMD ["catalina.sh", "run"]

# use the faroe entrypoint (replaces configuration properties with env vars)
ENTRYPOINT [ "/usr/local/bin/faroe-entrypoint.sh"]
