# Usa una imagen base de Maven para construir el proyecto
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de tu proyecto al contenedor
COPY pom.xml ./
COPY src ./src

# Ejecuta Maven para limpiar y empaquetar el proyecto
RUN mvn clean package -DskipTests

# Usa una imagen más ligera de Java para ejecutar la aplicación
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR generado en la etapa anterior
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto que usará la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]