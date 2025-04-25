# FreeToPlay Explorer 🎮

Aplicación JavaFX que consume la API de [FreeToGame](https://www.freetogame.com/api-doc) usando Retrofit y RxJava.  
Actividad de Aprendizaje para la asignatura **Programación de Servicios y Procesos** – 2º DAM (San Valero, 2025).

---

## 🧩 Tecnologías utilizadas

- 💻 Java 21
- ⚡ JavaFX (interfaz gráfica)
- 🔁 Retrofit + RxJava3 (programación reactiva y asincronía)
- 🌐 Spring Boot + WebFlux (para microservicio)
- 📄 Apache Commons CSV
- 📦 Compresión ZIP

---

## ✅ Funcionalidades implementadas

- 📋 **Visualización de juegos gratuitos** en una tabla (`TableView`)
- 🔍 **Búsqueda por título** (reactiva)
- 🖥️ **Filtro por plataforma** usando microservicio REST con WebFlux
- 🧠 **Filtro por categoría** realizado localmente sobre los datos cargados
- 💾 **Exportación a CSV + compresión ZIP** (usando `CompletableFuture` para concurrencia)
- 🔃 **Carga concurrente y no bloqueante** de datos mediante RxJava
- 🛰️ **Microservicio con Spring WebFlux** que actúa como proxy para obtener los juegos desde la API externa, transformando el atributo `genre` a `category` para integrarse con la app principal

---

## 🧠 Justificación técnica: Programación reactiva

Se ha implementado programación reactiva utilizando RxJava3 y Retrofit. A través de `Schedulers.io()` y `observeOn(...)`, las llamadas a la API se realizan de manera no bloqueante, separando la carga de red del hilo principal de JavaFX. 

Además, se incluye un microservicio adicional con Spring Boot + WebFlux, que responde de manera reactiva usando `WebClient` y expone un endpoint intermedio que es consumido por la aplicación JavaFX.

---

## 📡 Llamadas a la API utilizadas

1. `https://www.freetogame.com/api/games` → listado general (desde JavaFX vía Retrofit)
2. `/filtered-games?platform=pc` → juegos por plataforma (desde microservicio con WebFlux)

---

## 🚀 Cómo ejecutar

### Aplicación JavaFX

1. Clona el repositorio principal:
   ```bash
   git clone https://github.com/rungod95/freegames.git
   ```
2. Ejecuta con Maven:
   ```bash
   mvn clean javafx:run
   ```

### Microservicio (proyecto separado)

1. Clona el repositorio del microservicio:
   ```bash
   git clone https://github.com/rungod95/freegames-microservice.git
   ```
2. Ejecuta con:
   ```bash
   mvn spring-boot:run
   ```
   El microservicio se expondrá en `http://localhost:8080/filtered-games?platform=pc`

---

## 🎓 Autor

Javier Planas    
IES San Valero, Zaragoza – Curso 2024/2025
