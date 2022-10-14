FROM ubuntu:22.10 AS builder
# select bash as default shell
SHELL ["/bin/bash", "-c"]
WORKDIR /src
CMD [ "echo","Updating apt ..." ]
RUN apt update
CMD [ "echo","installing zip ..." ]
RUN apt install zip -y
CMD [ "echo","installing curl ..." ]
RUN apt install curl -y
CMD [ "echo","installing sdkman ..."]
RUN curl -s "https://get.sdkman.io" | bash
CMD [ "echo","installing graalvm, gradle, kotlin ..."]
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" \
    && sdk install java 22.2.r17-grl \
    && sdk install gradle 7.5.1 \
    && sdk install kotlin
CMD [ "echo","symlinking ..."]
ENV PATH=/root/.sdkman/candidates/java/current/bin:$PATH
ENV PATH=/root/.sdkman/candidates/gradle/current/bin:$PATH
ENV PATH=/root/.sdkman/candidates/kotlin/current/bin:$PATH
CMD [ "copying source code ..."]
COPY . .

RUN ./gradlew shadowJar

FROM amazoncorretto:17.0.4-alpine3.16
WORKDIR /root/
COPY --from=builder /src/build/libs/ewcharge-*-all.jar ./ewcharge.jar
EXPOSE 8080
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar ewcharge.jar