# Proyecto Marketplace - Arquitectura de Microservicios

Este repositorio contiene el código fuente de una plataforma tipo Marketplace desarrollada bajo una arquitectura de microservicios. Es un proyecto diseñado para gestionar usuarios, catalogo, etc.

## Equipo de Desarrollo 
* **Exequiel** - Desarrollo del microservicio de: usuarios, autenticacion, notificaciones y pagos (Carpeta `/EXE`)
* **Javier** - Desarrollo del microservicio de: busqueda, envio y pedidos (Carpeta `/JAVI`)
* **Nicolás** - Desarrollo del microservicio de: carro, catalogo e inventario (Carpeta `/NICO`)

## Tecnologías y Herramientas Utilizadas
* **Backend:** Java, framework Spring Boot.
* **Arquitectura:** Microservicios interconectados.
* **Seguridad:** Autenticación y autorización centralizada mediante JWT (JSON Web Tokens) y Spring Security.
* **Comunicación Interna:** Spring WebFlux (`WebClient`) para peticiones y orquestación reactiva entre microservicios.
* **Base de Datos:** MySQL con Spring Data JPA e Hibernate como ORM para la generación automática y gestión de las tablas (ddl-auto=update).
* **Validaciones y Manejo de Errores:** Validaciones de Jakarta (`@Valid`, `@NotNull`, `@Min`) y escudos interceptores de errores con `@ControllerAdvice` (`GlobalExceptionHandler`).
* **Control de Versiones:** Git y GitHub para la integración de ramas.
* **Pruebas y Documentación de API:** Postman.

## 📁 Estructura del Repositorio
Para mantener el orden de los microservicios, el repositorio está dividido en módulos independientes alojados en la raíz. Cada carpeta contiene su propio archivo `pom.xml` y configuración independiente.
