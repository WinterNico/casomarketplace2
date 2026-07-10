reemplazar el {{TOKEN}} en la 
pestaña "Authorization -> Bearer Token" de Postman en todos
los pasos siguientes después de hacer el Login.

---------------------------------------------------------
1. CREAR USUARIO (Registro de cliente)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/usuarios/registro

{
  "name": "Makoto Yuki",
  "email": "makoto@duocuc.cl",
  "password": "tartarus2026",
  "nameRol": "ROLE_USER"
}

---------------------------------------------------------
2. AUTENTICACIÓN (Login para obtener Token)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/auth/login

{
  "email": "makoto@duocuc.cl",
  "password": "tartarus2026"
}
-> IMPORTANTE: COPIAR EL TOKEN DEVUELTO AQUÍ

---------------------------------------------------------
3. CATÁLOGO (Crear el producto)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/catalogo/add

{
  "nombre": "Gitaroo Man (PS2)",
  "descripcion": "Juego original físico, impecable",
  "precio": 85000.0
}
-> ASUMIMOS QUE GENERA EL PRODUCTO CON ID: 1

---------------------------------------------------------
4. INVENTARIO (Inyectar stock a la bodega)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/inventory/add

{
  "productId": 1,
  "quantity": 5
}

---------------------------------------------------------
5. CARRO (Agregar producto al carrito)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/carro/add

{
  "userId": 1,
  "productId": 1,
  "quantity": 2
}

---------------------------------------------------------
6. PEDIDOS Y PAGOS (Orquesta compra y pago automático)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/pedidos

{
  "userId": 1,
  "total": 170000.0,
  "tarjeta": "5555666677778888" 
}
-> ASUMIMOS QUE GENERA EL PEDIDO CON ID: 1

---------------------------------------------------------
7. ENVÍOS (Generar la orden de despacho)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/envios

{
  "id": 1
}

---------------------------------------------------------
9. BÚSQUEDA (Agregador - Resumen final de todo el flujo)
---------------------------------------------------------
[GET] http://localhost:9090/api/v1/busqueda/1

*No requiere Body (JSON), solo enviar la petición con el Token en el Header.*

---------------------------------------------------------
8. NOTIFICACIONES (Simular envío de correo)
---------------------------------------------------------
[POST] http://localhost:9090/api/v1/notificaciones/enviar

{
  "emailDestino": "makoto@duocuc.cl",
  "asunto": "Confirmación de Pedido",
  "mensaje": "Tu pedido #1 ha sido procesado y el envío está en preparación."
}
