# Sistema de Gestión Escolar - Backend

Bienvenido al repositorio del Backend del Sistema de Gestión Escolar. Esta API RESTful, desarrollada en **Java con Spring Boot**, gestiona la lógica de negocio y la persistencia de datos del sistema educativo.

## Enlaces Importantes
- **Repositorio:** [GitHub Backend](https://github.com/YaelAdair11/proyecto_escolar_backend.git)
- **Despliegue (Render):** [https://proyecto-escolar-backend.onrender.com](https://proyecto-escolar-backend.onrender.com)
- **Video Demo:** [YouTube](https://youtu.be/swjdKHQIViQ)
- **Base de Datos:** PostgreSQL (Supabase)

---

## Introducción
El Portal de Gestión Escolar es una aplicación web moderna diseñada para simplificar y centralizar las tareas administrativas y académicas.

La arquitectura del backend se centra en exponer una API robusta que comunica el cliente (Frontend) con la base de datos en la nube. Se implementaron operaciones CRUD completas, manejo de relaciones entre entidades (Alumnos, Maestros, Materias) y optimización de conexiones mediante HikariCP.

##  Problemática
En el entorno educativo actual, la falta de sistemas centralizados genera:
1.  **Procesos Manuales:** Uso excesivo de papel y hojas de cálculo.
2.  **Información Fragmentada:** Datos desactualizados entre departamentos.
3.  **Falta de Visibilidad:** Dificultad para obtener reportes en tiempo real.

Este proyecto ofrece una solución tecnológica unificada para agilizar la gestión escolar.

---

##  Tecnologías
* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.3.0
* **Base de Datos:** PostgreSQL (Supabase)
* **Gestor de Dependencias:** Maven
* **Despliegue:** Docker + Render

---

## 👥 Equipo de Desarrollo

| Desarrollador | Rol / Funcionalidades |
| :--- | :--- |
| **Gutiérrez Contreras Yael Adair** | CRUD Alumnos, Turnos y Asignaciones. Optimización BD y Arquitectura Backend. |
| **Guzmán Zavaleta José Ángel** | Gestión de Maestros, Vistas Admin/Maestro. |
| **Herrera González Carolina** | Login (Auth), Seguridad y Estructura Frontend. |
| **Saldaña Marlene** | Inscripción, Calificaciones, Asistencia y Biblioteca. |
| **Suarez Salamanca Jonathan** | CRUD de Materias. |

---

## Ejecución en Local

### Prerrequisitos
* Java 17 o superior instalado.
* Maven instalado (o usar el wrapper `mvnw` incluido).

### Pasos
1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/YaelAdair11/proyecto_escolar_backend.git](https://github.com/YaelAdair11/proyecto_escolar_backend.git)
    cd proyecto_escolar_backend
    ```

2.  **Configurar Variables de Entorno:**
    Asegúrate de que el archivo `src/main/resources/application.properties` tenga las credenciales correctas de tu base de datos Supabase.

3.  **Compilar y Ejecutar:**
    Puedes usar el comando para compilar saltando los tests para mayor rapidez:
    ```bash
    ./mvnw clean package -DskipTests
    ```
    
    Luego, ejecuta el archivo `.jar` generado:
    ```bash
    java -jar target/gestion-0.0.1-SNAPSHOT.jar
    ```
    
    *El servidor iniciará en `http://localhost:8080`*

---

## Despliegue en la Nube (Render)

Este proyecto está configurado para desplegarse usando **Docker**.

1.  Ingresa a [Render](https://render.com) y regístrate con GitHub.
2.  Crea un **New Web Service**.
3.  En "Source Code", selecciona este repositorio (`proyecto_escolar_backend`).
4.  **Configuración:**
    * **Language:** Docker
    * **Instance Type:** Free
5.  Asegúrate de que el archivo `Dockerfile` esté en la raíz del proyecto.
6.  Clic en **Deploy Web Service**.

---

## Ejemplos de Peticiones (CURL)

### 1. Obtener todos los alumnos
curl -X GET [https://proyecto-escolar-backend.onrender.com/api/alumnos](https://proyecto-escolar-backend.onrender.com/api/alumnos)

### 1. Obtener todos los alumnos
```bash
curl -X GET [https://proyecto-escolar-backend.onrender.com/api/alumnos](https://proyecto-escolar-backend.onrender.com/api/alumnos)
