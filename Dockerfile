FROM ubuntu:22.10 AS builder
# select bash as default shell
SHELL ["/bin/bash", "-c"]
WORKDIR /src
RUN apt update
RUN apt install zip -y
RUN apt install curl -y
RUN curl -s "https://get.sdkman.io" | bash
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" \
    && sdk install java 22.2.r17-grl \
    && sdk install gradle 7.5.1 \
    && sdk install kotlin

ENV PATH=/root/.sdkman/candidates/java/current/bin:$PATH
ENV PATH=/root/.sdkman/candidates/gradle/current/bin:$PATH
ENV PATH=/root/.sdkman/candidates/kotlin/current/bin:$PATH

COPY . .

RUN ./gradlew shadowJar

FROM amazoncorretto:17.0.4-alpine3.16
WORKDIR /opt/ewcharge
COPY --from=builder /src/build/libs/ewcharge-*-all.jar ./ewcharge.jar
EXPOSE 8080
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar ewcharge.jar