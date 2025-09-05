# ---- Build stage: compile & package with Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copy only pom first to take advantage of Docker cache for dependencies
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# copy sources and build
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---- Runtime stage: small, secure image ----
# Use distroless JRE for minimal attack surface. If you prefer a full JRE, use eclipse-temurin:17-jre.
FROM gcr.io/distroless/java17-debian12

WORKDIR /app
# copy the produced jar from the build stage
COPY --from=build /app/target/*SNAPSHOT.jar /app/app.jar

# Container-friendly JVM defaults
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"
# If you use Gson and see InaccessibleObjectException on java.time, uncomment:
# ENV JAVA_TOOL_OPTIONS="$JAVA_TOOL_OPTIONS --add-opens=java.base/java.time=ALL-UNNAMED"

# Spring profile & port (override at runtime with -e or --env-file)
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 7777

# Run as non-root user for security (distroless user id 10001 typically exists)
USER 10001:10001

ENTRYPOINT ["java","-jar","/app/app.jar"]
