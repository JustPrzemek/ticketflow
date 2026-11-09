# Build context: repository root (the module needs the parent POM and the Maven wrapper).
#   docker build -f services/catalog-service/Dockerfile -t ticketflow-catalog-service .
#
# The two build stages run on the build machine's own architecture ($BUILDPLATFORM): a JAR is the
# same on every CPU, so there is no point in compiling it under emulation. Only the last stage is
# per-target-architecture, and it contains nothing but COPY, so no QEMU is needed for arm64.

# --- 1. Compile the executable JAR ------------------------------------------------------------
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk-noble AS build
WORKDIR /workspace

# POMs first: dependencies are downloaded in their own layer, which is reused until a POM changes.
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY services/catalog-service/pom.xml services/catalog-service/
RUN ./mvnw -B -ntp -pl services/catalog-service -am dependency:go-offline

COPY services/catalog-service/src services/catalog-service/src
# Tests already ran in the CI build job; the image build only packages.
RUN ./mvnw -B -ntp -pl services/catalog-service -am package -DskipTests \
 && cp services/catalog-service/target/catalog-service-*.jar /workspace/application.jar

# --- 2. Split the JAR into layers (rarely changing first) -------------------------------------
FROM --platform=$BUILDPLATFORM eclipse-temurin:25-jdk-noble AS extract
WORKDIR /workspace
COPY --from=build /workspace/application.jar .
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# --- 3. Runtime image (built once per target platform) ---------------------------------------
FROM eclipse-temurin:25-jre-noble
WORKDIR /app

# Each COPY is a separate image layer; a code change only replaces the last one.
COPY --from=extract /workspace/extracted/dependencies/ ./
COPY --from=extract /workspace/extracted/spring-boot-loader/ ./
COPY --from=extract /workspace/extracted/snapshot-dependencies/ ./
COPY --from=extract /workspace/extracted/application/ ./

# Numeric UID so Kubernetes `runAsNonRoot` can verify it (1000 = "ubuntu" in the base image).
USER 1000:1000
EXPOSE 8081
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["java", "-jar", "application.jar"]
