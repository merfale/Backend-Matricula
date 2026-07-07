# =========================================================
# Etapa 1: Compilación (build) del proyecto con Maven
# =========================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiamos primero solo el pom.xml para aprovechar la cache de capas de Docker:
# si no cambian las dependencias, no se vuelven a descargar en cada build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente y compilamos el .jar (sin correr los tests)
COPY src ./src
RUN mvn clean package -DskipTests -B


# =========================================================
# Etapa 2: Imagen final de ejecución (solo el JRE, más liviana)
# =========================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos únicamente el .jar ejecutable generado en la etapa de build
# (el patrón excluye el *.jar.original que genera spring-boot-maven-plugin)
COPY --from=build /app/target/matricula-backend-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
