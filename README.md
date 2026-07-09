# 🛒 E-Commerce API - Arquitectura de Microservicios
Este proyecto es un sistema de E-Commerce backend basado en una arquitectura de microservicios desarrollada con **Java Spring Boot**. El sistema orquesta todo el flujo de compras, desde el registro de usuarios y gestión de catálogo, hasta la pasarela de pagos, control de inventario y notificaciones.

## Tecnologías Utilizadas
* **Java 21**
* **Spring Boot** (Web, Data JPA, Validation, WebFlux/WebClient)
* **Spring Cloud Netflix Eureka** (Service Discovery)
* **MySQL** (Bases de datos independientes por microservicio)
* **Docker & Docker Compose** (Contenerización y orquestación)
* **Maven** (Gestor de dependencias)

## Estructura de Microservicios
El ecosistema está compuesto por los siguientes servicios:
1. `ms-eureka`: Servidor de descubrimiento (Registry).
2. `usuarios`: Gestión de clientes y roles.
3. `autenticacion`: Autenticación (JWT).
4. `catalogo`: Administración de productos y detalles.
5. `inventario`: Control de stock en bodega.
6. `carro`: Gestión del carrito de compras por usuario.
7. `pedidos`: Orquestador principal del proceso de checkout.
8. `pagos`: Pasarela de transacciones y validaciones de tarjetas.
9. `envio`: Logística y generación de números de seguimiento (Tracking).
10. `notificaciones`: Simulación de envío de correos electrónicos.
11. `busqueda`: Patrón API Composition / Agregador que unifica los datos del pedido, envío y usuario.
12. `api-gateway`: Donde se organiza el trafico del sistema.

---

## ⚙️ Instrucciones de Levantamiento

Para ejecutar este proyecto desde cero y evitar problemas de sincronización de versiones, por favor sigue estos pasos al pie de la letra:

### Paso 1: Compilar los Microservicios
Antes de levantar los contenedores, es **obligatorio** compilar el código fuente para generar los archivos `.jar` actualizados de cada microservicio.

Puedes hacerlo desde tu IDE (ej. IntelliJ IDEA) usando la pestaña de Maven ejecutando `clean` y luego `package` en cada carpeta, o ejecutando el siguiente comando en la raíz de cada microservicio desde la terminal:
`mvn clean package`

### Paso 2: Construir las Imágenes de Docker
Una vez que todos los microservicios tengan su archivo `.jar` generado en sus respectivas carpetas `target/`, ubícate en la raíz del proyecto (donde se encuentra el archivo `docker-compose.yml`) y ejecuta la construcción de las imágenes sin usar la caché para asegurar la versión más reciente:
`docker-compose build --no-cache` o en su defecto `docker-compose build`

### Paso 3: Levantar la Arquitectura
Inicia todos los contenedores (Bases de datos, Eureka y Microservicios) en segundo plano:
`docker-compose up -d`
*(Nota: Eureka y las bases de datos tardan unos segundos en estar 100% operativos. Se recomienda esperar un momento antes de lanzar peticiones).*

---

## 🧹 Limpieza del Entorno
Si necesitas reiniciar la base de datos desde cero (eliminar registros de prueba, limpiar IDs, etc.), apaga los contenedores destruyendo los volúmenes con el siguiente comando:
`docker-compose down -v`
Al volver a ejecutar `docker-compose up -d`, las bases de datos se crearán completamente limpias.

---

## 🧪 Pruebas de la API (Postman)
Todas las peticiones pasan a través del puerto unificado `9090` (`http://localhost:9090/api/v1/...`). 
Para probar el flujo completo, sigue este orden de ejecución:

1. **Usuarios:** Registrar un usuario 
2. **Autenticacion:** Hacer Login (email y contraseña) para obtener el Token de autorización (Bearer Token), se ocupara en todas las siguientes.
3. **Catálogo:** Crear un producto.
4. **Inventario:** Inyectar stock a ese producto recién creado.
5. **Carro:** Agregar el producto al carro de compras del usuario.
6. **Pedidos:** Hacer el Checkout (Orquesta el pago automáticamente validando la tarjeta).
7. **Envíos:** Generar la orden de despacho con el ID del pedido.
8. **Notificaciones:** Enviar correo simulado de confirmación.
9. **Búsqueda:** Consultar el pedido para obtener el JSON consolidado con toda la información.

## Peticiones hacia POSTMAN (JSON)
**[Ver Guía Completa de Pruebas Postman](./GUIA_POSTMAN.md)**