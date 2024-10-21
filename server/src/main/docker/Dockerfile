FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /opt/franklyn

COPY pom.xml .

RUN mvn verify clean --fail-never

COPY . .

RUN mvn -B -Dmaven.test.skip package

FROM linuxserver/ffmpeg:7.1-cli-ls4

# setup java
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:21 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# copy artifact from build step
WORKDIR /opt/franklyn
COPY --from=builder /opt/franklyn/target/*-runner.jar /opt/franklyn/server.jar
EXPOSE 8080

ENTRYPOINT [ "java",  "-jar", "server.jar" ]
