# Domain Models - NexusMarket

---

## Usuario (Clase Padre)

### Descripción
Representa a cualquier persona autorizada para interactuar con el sistema NexusMarket.
Esta clase centraliza la información de identidad compartida por todos los participantes.

### Atributos

| Atributo | Tipo | Descripción |
| :--- | :--- | :--- |
| identificador | String | Identifica de forma única al usuario. Debe ser único. |
| nombreCompleto | String | Nombre oficial del usuario. No puede estar vacío. |
| correoElectronico | String | Medio principal de acceso y comunicación. |
| rol | String | Define las responsabilidades y permisos. Es único por usuario. |
| estado | String | Condición operativa (Activo, Bloqueado, etc.). |

### Relaciones
* Un `Usuario` puede especializarse como un `Comprador`.
* Un `Usuario` puede especializarse como un `Vendedor`.

---

## Comprador

### Descripción
Representa a la persona que adquiere productos publicados en el Marketplace.
No administra información de otros compradores ni inventarios.

### Atributos

| Atributo | Tipo | Descripción |
| :--- | :--- | :--- |
| direccionPrincipal | String | Ubicación habitual para entregas. (Obligatorio) |
| direccionesAdicionales | List<String> | Ubicaciones secundarias de entrega. (Opcional) |
| estadoComercial | String | Condición del comprador para realizar compras. |
---

## Vendedor

### Descripción
Responsable de registrar, comercializar y administrar sus productos en el catálogo[cite: 1]. Son incorporados exclusivamente por el Administrador[cite: 1].

### Atributos
(No posee atributos adicionales a los heredados para esta fase operativa).

### Relaciones
* Hereda de la clase `Usuario`.
* Un `Vendedor` registra y administra múltiples `Producto`s[cite: 1].

---

## Producto

### Descripción
Bienes físicos o digitales ofrecidos en el catálogo del Marketplace[cite: 1].

### Atributos

| Atributo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | String | Identificador único del producto. |
| tipoProducto | String | Define si es Físico (requiere despacho) o Digital (entrega inmediata)[cite: 1]. |
| variantes | List<String> | Diferencias de color, talla, modelo, etc[cite: 1]. |
| estado | String | Publicado, Suspendido o Descontinuado[cite: 1]. |

### Relaciones
* Pertenece a un `Vendedor`.
* Tiene asociado un `Inventario` físico.

---

## Bodega e Inventario

### Descripción
La `Bodega` controla los espacios físicos de almacenamiento, mientras que el `Inventario` administra las existencias disponibles para comercialización[cite: 1].

### Atributos (Inventario)

| Atributo | Tipo | Descripción |
| :--- | :--- | :--- |
| existencias | int | Cantidad disponible. No se permitirán existencias negativas bajo ninguna circunstancia[cite: 1]. |

### Relaciones
* El `Inventario` es distribuido y debe estar vinculado obligatoriamente a un `Producto` y una `Bodega` específica[cite: 1].

---

## Pedido

### Descripción
Representa el compromiso comercial formal y su ciclo de vida es el proceso central del sistema[cite: 1].

### Atributos

| Atributo | Tipo | Descripción |
| :--- | :--- | :--- |
| id | String | Identificador único del pedido. |
| estado | String | Su ciclo es: Carrito, Pendiente de Pago, Pagado, Despachado, Entregado / Finalizado[cite: 1]. |

### Relaciones
* Pertenece a un `Comprador`.
* Contiene una lista de `Producto`s seleccionados.
* Genera procesos logísticos asociados como la `Factura` y el `Envio`.
### Relaciones
* Hereda de la clase `Usuario`.
* Un `Comprador` gestiona un `CarritoCompras`.
* Un `Comprador` genera uno o múltiples `Pedido`s.