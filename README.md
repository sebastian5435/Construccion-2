# NexusMarket API

REST API for **NexusMarket**, a marketplace that intermediates between buyers and sellers and manages users, warehouses, catalog, distributed inventory, shopping cart, orders, invoicing, shipments, returns, refunds, and administrative reports.

Built with **Spring Boot 4.1**, **Java 17+**, and **MySQL**, following a **Hexagonal Architecture** (Ports & Adapters).

The domain design is documented in [`SSD/domain`](SSD/domain):
[Domain Model](SSD/domain/Domain%20Model.md) ·
[Value Objects](SSD/domain/Value%20objects.md) ·
[Domain Services](SSD/domain/Domain%20Services.md) ·
[Input Ports](SSD/domain/Input-ports.md) ·
[Output Ports](SSD/domain/Output-ports.md)

---

## Architecture

```text
bank/bank/src/main/java/application
├── domain                         ← business core, no knowledge of HTTP or SQL
│   ├── models                     entities with their own rules (Order, Inventory, Product…)
│   ├── valueobjects               enums for roles, statuses and types
│   ├── exceptions                 business exceptions
│   ├── ports/input                use cases (what the system can do)
│   ├── ports/output               repository contracts (what the domain needs)
│   └── services                   use-case implementations + AccessGuard + StockMovementService
└── adapters                       ← infrastructure
    ├── web                        REST controllers, DTOs, GlobalExceptionHandler
    ├── persistence/jpa            MySQL: JPA entities, Spring Data repositories, mapper, adapters
    ├── persistence/inmemory       in-memory adapters (profile "inmemory")
    └── config                     startup configuration (first administrator)
```

Request flow: `Controller → Input Port (use case) → Domain Service → Entities → Output Port → JPA Adapter → MySQL`.

---

## Running the application

### 1. Start MySQL

The application connects to `localhost:3306` and creates the `nexusmarket` database automatically (`createDatabaseIfNotExist=true`). Tables are created/updated by Hibernate (`ddl-auto=update`).

### 2. Configure credentials (environment variables)

| Variable | Default | Description |
| :--- | :--- | :--- |
| `DB_URL` | `jdbc:mysql://localhost:3306/nexusmarket?createDatabaseIfNotExist=true&serverTimezone=UTC` | JDBC URL |
| `DB_USERNAME` | `root` | MySQL user |
| `DB_PASSWORD` | *(empty)* | MySQL password |
| `ADMIN_IDENTIFIER` | `1000000000` | Identifier of the first administrator |
| `ADMIN_EMAIL` | `admin@nexusmarket.com` | Email of the first administrator |

PowerShell example:

```powershell
$env:DB_PASSWORD = "your-password"
```

### 3. Run

```bash
cd bank/bank
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`. On the first start, an **initial Administrator** is created (identifier `1000000000` unless `ADMIN_IDENTIFIER` is set), because every other administrative user must be registered by an existing Administrator.

### Tests

```bash
cd bank/bank
./mvnw test
```

* `MarketplaceFlowTest`: business rules of the domain services (in-memory adapters).
* `NexusMarketApiTest`: complete purchase flow through the REST API with JPA on an H2 database in MySQL mode.

---

## Identifying the requester

Every operation that depends on the role of the participant receives the header:

```
X-User-Id: <user identifier>
```

The domain validates that the user exists, is `ACTIVE`, and has the role required by the use case (RG-01, RG-02, RG-03). A missing header returns `401`.

## Error responses

| HTTP | When |
| :--- | :--- |
| `400` | Invalid fields or malformed body (`details` lists each invalid field). |
| `401` | Missing `X-User-Id` header. |
| `403` | The requester's role is not allowed to execute the operation. |
| `404` | The requested entity does not exist. |
| `409` | Conflict with the current state: duplicated email/identifier, invalid order transition, finalized order, insufficient stock. |
| `422` | Any other business rule violation. |

```json
{
  "timestamp": "2026-10-23T10:15:30",
  "status": 403,
  "error": "Forbidden",
  "message": "Operation 'payOrder' requires role: the buyer who placed the order",
  "path": "/api/orders/6f1c.../pay",
  "details": []
}
```

---

## Endpoints

### Users (OBJ-01, OBJ-02, OBJ-03)

| Method | Path | Role (`X-User-Id`) | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/buyers` | — | Buyer self-registration |
| POST | `/api/sellers` | Administrator | Register a seller with their first warehouse |
| POST | `/api/logistics-operators` | Administrator | Register a logistics operator |
| POST | `/api/supervisors` | Administrator | Register a supervisor |
| POST | `/api/administrators` | Administrator | Register another administrator |
| PATCH | `/api/users/{userId}/block` | Administrator | Block a user |
| PATCH | `/api/users/{userId}/activate` | Administrator | Reactivate a user |
| GET | `/api/users` | Administrator | List all users |
| GET | `/api/users/{userId}` | — | Find a user |
| GET | `/api/users/search?email=` | — | Find a user by email |

### Warehouses (OBJ-04)

| Method | Path | Role | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/warehouses` | Administrator | Register a Marketplace warehouse |
| POST | `/api/sellers/{sellerId}/warehouses` | Administrator | Register an additional seller warehouse |
| GET | `/api/warehouses?type=MARKETPLACE\|SELLER` | — | List warehouses (optional filter) |
| GET | `/api/warehouses/{warehouseId}` | — | Find a warehouse |
| GET | `/api/sellers/{sellerId}/warehouses` | — | Warehouses of a seller |

### Catalog (OBJ-05)

| Method | Path | Role | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/products` | Seller | Create a product (starts `SUSPENDED`) |
| PUT | `/api/products/{productId}` | Owner seller | Update a product |
| PATCH | `/api/products/{productId}/publish` | Owner seller | Publish (physical products need stock) |
| PATCH | `/api/products/{productId}/suspend` | Owner seller | Suspend |
| PATCH | `/api/products/{productId}/discontinue` | Owner seller | Discontinue permanently |
| GET | `/api/products` | — | Public catalog (published products) |
| GET | `/api/products/{productId}` | — | Find a product |
| GET | `/api/sellers/{sellerId}/products` | — | Products of a seller |

### Inventory (OBJ-06)

| Method | Path | Role | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/inventory` | Owner seller / Operator | Register stock of a product in a warehouse |
| POST | `/api/inventory/{productId}/{warehouseId}/entries` | Owner seller / Operator | Add units (ENTRY) |
| PUT | `/api/inventory/{productId}/{warehouseId}/quantity` | Owner seller / Operator | Adjust the physical count (ADJUSTMENT) |
| PATCH | `/api/inventory/{productId}/{warehouseId}/damaged` | Owner seller / Operator | Mark as damaged |
| PATCH | `/api/inventory/{productId}/{warehouseId}/available` | Owner seller / Operator | Mark as available |
| GET | `/api/inventory/{productId}/{warehouseId}/availability` | — | Available units |
| GET | `/api/inventory/{productId}/{warehouseId}/movements` | — | Movement history |
| GET | `/api/inventory?productId=` or `?warehouseId=` | — | List inventory records |

### Shopping cart (OBJ-07)

All endpoints use the buyer's `X-User-Id`.

| Method | Path | Description |
| :--- | :--- | :--- |
| POST | `/api/cart` | Create the cart |
| GET | `/api/cart` | Get the cart |
| POST | `/api/cart/items` | Add a product |
| PUT | `/api/cart/items/{productId}` | Change the quantity |
| DELETE | `/api/cart/items/{productId}` | Remove a product |
| DELETE | `/api/cart/items` | Empty the cart |

### Orders and invoicing (OBJ-08, OBJ-09)

| Method | Path | Role | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/orders` | Buyer | Place an order from the cart (optional body `{"deliveryAddress": "..."}`) |
| PATCH | `/api/orders/{orderId}/pay` | Owner buyer | Pay; generates the invoice |
| PATCH | `/api/orders/{orderId}/cancel` | Owner buyer | Cancel a pending order |
| GET | `/api/orders` | Buyer | My orders |
| GET | `/api/orders/{orderId}` | — | Find an order |
| GET | `/api/orders/{orderId}/invoice` | — | Invoice of an order |

### Shipments (OBJ-10)

| Method | Path | Role | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/shipments` | Operator | Create the shipment of a paid order |
| PATCH | `/api/shipments/{trackingId}/dispatch` | Assigned operator | Dispatch (consumes stock) |
| PATCH | `/api/shipments/{trackingId}/deliver` | Assigned operator | Confirm delivery |
| GET | `/api/shipments` | Operator | My shipments |
| GET | `/api/shipments/{trackingId}` | — | Find a shipment |
| GET | `/api/orders/{orderId}/shipment` | — | Shipment of an order |

### Returns and refunds (OBJ-11)

| Method | Path | Role | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/returns` | Owner buyer | Request a return of a delivered order |
| POST | `/api/returns/{returnId}/refund` | Administrator | Approve the return and issue the refund |
| PATCH | `/api/returns/{returnId}/reject` | Administrator | Reject the return |
| GET | `/api/returns/pending` | Administrator | Returns awaiting resolution |
| GET | `/api/returns/{returnId}` | — | Find a return |
| GET | `/api/returns/{returnId}/refund` | — | Refund of a return |
| GET | `/api/refunds/{refundId}` | — | Find a refund |

### Reports (OBJ-12)

| Method | Path | Role | Description |
| :--- | :--- | :--- | :--- |
| GET | `/api/reports/operational` | Supervisor / Administrator | Consolidated operational report |
| GET | `/api/reports/orders?status=PAID` | Supervisor / Administrator | Orders in a given status |

---

## Example: complete purchase flow

```bash
# 1. Administrator registers a seller with their first warehouse
curl -X POST localhost:8080/api/sellers -H "X-User-Id: 1000000000" -H "Content-Type: application/json" \
  -d '{"identifier":"900100","fullName":"Tech Store","email":"tech@store.com","firstWarehouse":{"name":"Tech Store Main","address":"Calle 10 # 20-30"}}'

# 2. Administrator registers a logistics operator; a buyer self-registers
curl -X POST localhost:8080/api/logistics-operators -H "X-User-Id: 1000000000" -H "Content-Type: application/json" \
  -d '{"identifier":"800200","fullName":"Fast Logistics","email":"ops@fast.com"}'
curl -X POST localhost:8080/api/buyers -H "Content-Type: application/json" \
  -d '{"identifier":"100300","fullName":"Ana Gómez","email":"ana@mail.com","mainAddress":"Carrera 50 # 10-20"}'

# 3. Seller creates a product; stock is registered; seller publishes it
curl -X POST localhost:8080/api/products -H "X-User-Id: 900100" -H "Content-Type: application/json" \
  -d '{"name":"Keyboard","price":150.0,"productType":"PHYSICAL","variants":["Black"]}'
curl "localhost:8080/api/sellers/900100/warehouses"
curl -X POST localhost:8080/api/inventory -H "X-User-Id: 900100" -H "Content-Type: application/json" \
  -d '{"productId":"<productId>","warehouseId":"<warehouseId>","initialQuantity":10}'
curl -X PATCH localhost:8080/api/products/<productId>/publish -H "X-User-Id: 900100"

# 4. Buyer adds to cart, places and pays the order
curl -X POST localhost:8080/api/cart/items -H "X-User-Id: 100300" -H "Content-Type: application/json" \
  -d '{"productId":"<productId>","quantity":2}'
curl -X POST localhost:8080/api/orders -H "X-User-Id: 100300"
curl -X PATCH localhost:8080/api/orders/<orderId>/pay -H "X-User-Id: 100300"

# 5. Operator creates, dispatches and delivers the shipment
curl -X POST localhost:8080/api/shipments -H "X-User-Id: 800200" -H "Content-Type: application/json" \
  -d '{"orderId":"<orderId>"}'
curl -X PATCH localhost:8080/api/shipments/<trackingId>/dispatch -H "X-User-Id: 800200"
curl -X PATCH localhost:8080/api/shipments/<trackingId>/deliver -H "X-User-Id: 800200"
```
