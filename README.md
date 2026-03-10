# 🔐 Password Recovery Microservice (Reactive)

Microservicio reactivo de recuperación de contraseña desarrollado con **Java 17 + Spring WebFlux + SQL Server**.

Implementa manejo global de errores, programación reactiva y arquitectura por capas.

---

## 🚀 Tecnologías

- Java 17
- Spring Boot
- Spring WebFlux (Reactive)
- R2DBC
- MSSQL Server
- Gradle
- Mockito (Testing)
- Docker

---

## 🧠 Arquitectura

El proyecto sigue una arquitectura por capas:

Controller → Service → Repository → Database

### Principios aplicados:

- ✅ Single Responsibility
- ✅ Dependency Inversion
- ✅ Clean Code
- ✅ Manejo global de excepciones
- ✅ Programación reactiva (Mono)

---

## 📌 Características

- Generación de token único (UUID)
- Expiración automática (10 minutos)
- Token de un solo uso
- Validación segura del token
- Manejo profesional de errores
- API reactiva no bloqueante

---

## 📡 Endpoints

### 🔹 Solicitar recuperación

POST `/password/request?email=user@mail.com`

---

### 🔹 Validar token

GET `/password/validate?token=xxxxx`

---

### 🔹 Confirmar token

POST `/password/confirm?token=xxxxx`

---

## 🧪 Testing

Se utilizan pruebas unitarias con Mockito para validar la lógica del servicio sin depender de la base de datos.

---
