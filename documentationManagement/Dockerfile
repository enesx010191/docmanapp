# Multi-stage build için Eclipse Temurin JDK 21 kullanıyoruz
FROM eclipse-temurin:21-jdk-alpine AS build

# Çalışma dizini belirleme
WORKDIR /app

# Maven wrapper ve pom.xml dosyalarını kopyalama
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Maven wrapper yetkilerini ayarlama (Alpine için gerekli)
RUN chmod +x ./mvnw

# Bağımlılıkları indirme (cache optimizasyonu için)
RUN ./mvnw dependency:go-offline

# Kaynak kodları kopyalama
COPY src src

# Uygulamayı build etme
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Çalışma dizini
WORKDIR /app

# Build stage'den jar dosyasını kopyalama
COPY --from=build /app/target/documentationManagement-0.0.1-SNAPSHOT.jar app.jar

# Port açma (Spring Boot default port)
EXPOSE 8080

# Uygulamayı çalıştırma
ENTRYPOINT ["java", "-jar", "app.jar"]