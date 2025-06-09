# Api Ejercicio GlobalLogic

## Descripción
Este proyecto es una API REST construida en Java con Spring Boot como parte de un proceso de selección técnica para GlobalLogic.

## 📌 Requisitos

- SpringBoot 2.5.14
- Java 11
- Gradle 7.4 Kotlin DSL
- Junit/Mockito
- JWT

## 🚀 Cómo ejecutar el proyecto

```bash
# Clonar el repositorio
git clone https://github.com/Velthraz/ejercicio-globallogic.git
cd ejercicio-globallogic

# Compilar y ejecutar con Gradle
./gradlew bootRun
```
## 🛡️ Autenticación
El endpoint de login requiere un JWT válido, puede estar expirado pero debe pertenecer al usuario. Este debe enviarse en la cabecera:
- Authorization: Bearer <token>

## 🔐 Endpoints
- POST /sign-up — Crea un usuario y genera un token JWT valido por 5 minutos.
- POST /login — Genera un token JWT teniendo las credenciales y un token valido para el usuario.

## 📥 Ejemplos de uso
**Request /sign-up:**

```json
{
    "name": "spavez",
    "email": "ignacio.pavez.p@gmail.com",
    "password": "a2asfGfdfdf4",
    "phones": [
        {
            "number": 967890794,
            "citycode": 2,
            "contrycode": "56"
        }
    ]
}
```
```bash
http://localhost:8080/sign-up
```

**Request /login utilizar Bearer Token Authorization:**

```json
{
    "user": "spavez",
    "password": "a2asfGfdfdf4"
}
```
```bash
http://localhost:8080/login
```

## 📄 Estructura del proyecto
- src/
  - configuration/ 
  - controller/
  - dto/
  - exception/
    - handler/
  - model/
  - repository/
  - security/
      - util/
  - service/
 
## 📦 Dependencias principales
- Spring Boot
- Spring Security
- Spring Web
- Swagger / OpenAPI
- JWT (Json Web Token)

## 📬 Contacto
Sebastián Ignacio Pavez Perez
Email: ignacio.pavez.p@gmail.com
