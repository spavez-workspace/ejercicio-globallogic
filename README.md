# Api Ejercicio GlobalLogic

## Descripción
Este proyecto es una API REST construida en Java con Spring Boot como parte de un proceso de selección técnica para GlobalLogic.

## Diagramas
Los diagramas estan disponibles en /diagramas ubicada en la carpeta base del proyecto.

## 📌 Requisitos

- SpringBoot 2.5.14
- Java 11
- Gradle 7.4 Kotlin DSL
- Junit/Mockito
- JWT

## 🧠 Documentación Swagger
La documentación swagger se puede encontrar una vez iniciado el proyecto en la ruta
```bash
http://localhost:8080/swagger-ui/index.html
```

## 🚀 Cómo ejecutar el proyecto

```bash
# Clonar el repositorio
git clone https://github.com/spavez-workspace/ejercicio-globallogic.git
cd ejercicio-globallogic

# Compilar y ejecutar con Gradle
gradlew bootRun
```
## ⚙️ Configuración application.properties
Para modificar el puerto utilizado por la API se puede modificar
```bash
#El endpoint Swagger comparte puerto con la API
server.port=8080
```

Para modificar la Key o la duración de los tokens generados por JWT
```bash
#La llave debe ser administrada por un gestionador de secretos como Vault
jwt.secret=thisIsMysecregtfrdesww233eggtffeeddgkjjhhtdhttebd54ndhdhfhhhshs8877465sbbdd
#Tiempo de vida del token en segundos
jwt.expiration=300
```

## 🛡️ Autenticación
El endpoint de login requiere un JWT válido, puede estar expirado pero debe pertenecer al usuario. Este debe enviarse como Header de autorización:
- Ej: Authorization: Bearer eyJhbGciOiJIUzI1Ni....

## 🔐 Endpoints
- POST /api/users/sign-up — Crea un usuario y genera un token JWT valido por 5 minutos.
- POST /api/users/login — Genera un token JWT dado un token pasado por el Header Authorization
- POST /api/users/loginByRequestBody — Genera un token JWT teniendo las credenciales y un token valido para el usuario.

## 📥 Ejemplos de uso
**Request api/users/sign-up:**

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
http://localhost:8080/api/users/sign-up
```

**Request api/users/loginByRequestBody utilizar Bearer Token Authorization:**

```json
{
    "email": "ignacio.pavez.p@gmail.com",
    "password": "a2asfGfdfdf4"
}
```
```bash
http://localhost:8080/api/users/loginByRequestBody
```

**Request api/users/login utilizar Bearer Token Authorization:**

```bash
http://localhost:8080/api/users/login
```

## 🧪 Tests
Existe un Test para cada Controlador y uno para cada metodo publico del Service.
Para ejecutarlos:
```bash
gradlew test
```
El reporte de errores se genera en
```bash
/build/reports/tests/test/index.html
```

## 📊 Jacoco Report
Reporte de covertura de pruebas, apuntado a Service y Controller
```bash
gradlew jacocoTestReport
```
El reporte de errores se genera en
```bash
/build/reports/jacoco/test/html/index.html
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
