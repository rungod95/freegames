# FreeToPlay Explorer 🎮

Aplicación JavaFX que consume la API de [FreeToGame](https://www.freetogame.com/api-doc) usando Retrofit y RxJava.  
Actividad de Aprendizaje para la asignatura **Programación de Servicios y Procesos** – 2º DAM (San Valero, 2025).

---

## 🧩 Tecnologías utilizadas

- 💻 Java 21
- ⚡ JavaFX (interfaz gráfica)
- 🔁 Retrofit + RxJava3 (programación reactiva y asincronía)
- 🌐 WebFlux (para microservicio)
- 📄 Apache Commons CSV
- 📦 Compresión ZIP

---

## ✅ Funcionalidades implementadas

- 📋 **Visualización de juegos gratuitos** en una tabla (`TableView`)
- 🔍 **Búsqueda por título**
- 🖥️ **Filtro por plataforma** usando llamada directa a la API
- 🧠 **Filtro por categoría** realizado localmente sobre los datos cargados
- 💾 **Exportación a CSV + compresión ZIP**
- 🔃 **Carga concurrente y no bloqueante** de datos mediante RxJava
- 🛰️ **Microservicio con Spring WebFlux** para transformar y exponer datos

---

## 📡 Llamadas a la API utilizadas

1. `/games` → listado general
2. `/games?platform=pc` → juegos por plataforma

---

## 🚀 Cómo ejecutar

1. Clona el repositorio:
   `
   git clone https://github.com/rungod95/freegames.git`
2. Ejecuta con Maven:
   `
    mvn clean javafx:run`
   
3. Requisitos:
   
     Tener Java 21 instalado

     Tener configurado Maven
 
      IDE recomendado: IntelliJ IDEA
   

## 🎓 Autor

Javier Planas (rungod95)

Actividad de aprendizaje como entrega de la 2ª Evaluación

IES San Valero, Zaragoza – Curso 2024/2025
