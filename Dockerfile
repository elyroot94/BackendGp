# Étape 1 : Build l'application avec Maven
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Définir le répertoire de travail pour Maven
WORKDIR /build

# Copier les fichiers du projet dans le conteneur
COPY pom.xml ./
COPY src ./src

# Construire le fichier JAR
RUN mvn clean package -DskipTests

# Étape 2 : Image de production avec OpenJDK
FROM openjdk:21-jdk-slim

# Définir le répertoire de travail dans le conteneur
WORKDIR /app

# Copier le fichier JAR généré dans le conteneur à partir de l’étape de build
COPY --from=builder /build/target/aichagp-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port sur lequel l'application écoute
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]