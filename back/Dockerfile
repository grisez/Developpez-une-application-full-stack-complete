FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src ./src
RUN ./mvnw -B -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 appuser
COPY --from=build /workspace/target/*.jar app.jar
USER appuser
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]