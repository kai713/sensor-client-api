#  **Weather API and Client**

![Java](https://img.shields.io/badge/Java-17-blue?style=flat-square&logo=java)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green?style=flat-square&logo=spring)  
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=flat-square&logo=postgresql)  

## 📦 **Установка и запуск**

### 1️⃣ Склонируйте репозиторий:

```bash
git clone https://github.com/kai713/sensor-client-api.git
cd sensor-client-api
```

### 2️⃣ Dockerfile и запуск приложение:

- для запуска WeatherAPI (demo): cd demo 
- docker compose up --build (для того чтобы создать образ поднять контейнер)
- WeatherAPI (demo) будет доступен по пути: http://localhost:8080

- для запуска Client (RestClientCheck): cd RestClientCheck
- И запуститие после RestClientCheck (main класс), УБЕДЕТИСЬ что запустили WeatherAPI (demo) перед тем как запускать Client (RestClientCheck) !!!



### Функционал:
```bash
  swagger path: http://localhost:8080/swagger-ui/index.html

  Управление измерениями и сенсорами
  Авторизация и роли, смена роли, также имеется refreshToken
  Документация и все тесты (RefreshTokenServiceTest, SensorServiceTest итд) 
```

### Endpoints:
```bash
  Для документации с swagger: http://localhost:8080/swagger-ui/index.html

  POST http://localhost:8080/auth/register регистрация с json с параметрами: name, phoneNumber, email, password, возвращает statusCode
  POST http://localhost:8080/auth/login логин c json с параметрами: email, password, возвращает statusCode
  POST http://localhost:8080/auth/refreshToken принимает json refreshToken, возвращает accessToken
  PATCH http://localhost:8080/auth/changeRole принимает json с названием роли ("role" : "ADMIN"), id береться из контекста security 

  POST http://localhost:8080/measurements/add для добавленмя измерения json с параметрами: value, isRaining, sensor (object)
  GET http://localhost:8080/measurements для просмотра всех измерении, возвращает List с измерениями в формате json
  POST http://localhost:8080/sensors/registration принимает name ("name" : "SensorName")
```
