#!/usr/bin/env sh

set -e

echo "Building the application..."

# Ensure mvnw is executable (important in Linux container)
chmod +x mvnw

./mvnw clean package -DskipTests -q

echo "Starting the application on port 8080..."

JAR_FILE=$(ls target/*.jar | head -n 1)

exec java -jar "$JAR_FILE" --server.port=8080