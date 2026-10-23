# Input Ports

## Introduction

Input Ports are the entry point of the domain. They are **interfaces** that describe *what the system can do* without saying *how* it does it or *who* calls it.

Following the Hexagonal Architecture pattern:

* Every use case defined in the functional specification is expressed as a method on an Input Port.
* Domain Services implement these interfaces and contain the business logic.
* External adapters (REST controllers, tests) invoke the system by depending only on the Input Ports, never on the concrete services.

---

## Input Port Inventory

```text
UserManagementUseCase          → OBJ-01, OBJ-02, OBJ-03
WarehouseManagementUseCase     → OBJ-04
ProductCatalogUseCase          → OBJ-05
InventoryManagementUseCase     → OBJ-06
ShoppingCartUseCase            → OBJ-07
OrderProcessingUseCase         → OBJ-08, OBJ-09
ShipmentManagementUseCase      → OBJ-10
ReturnRefundUseCase            → OBJ-11
MarketplaceReportUseCase       → OBJ-12
```

---

## UserManagementUseCase

| Method | Allowed role | Description |
| :--- | :--- | :--- |
| `registerBuyer(Buyer)` | Anyone | Self-registration of a Buyer. Main address required. |
| `registerSeller(Seller, firstWarehouse, adminId)` | Administrator | Registers a Seller together with their first warehouse. |
| `registerLogisticsOperator(LogisticsOperator, adminId)` | Administrator | Registers a Logistics Operator. |
| `registerSupervisor(Supervisor, adminId)` | Administrator | Registers a Supervisor. |
| `registerAdministrator(Administrator, requesterAdminId)` | Administrator | Registers another Administrator. |
| `blockUser(userId, adminId)` | Administrator | Blocks a user (not themselves). |
| `activateUser(userId, adminId)` | Administrator | Reactivates a blocked user. |
| `findUserById(id)` | — | Retrieves a user by identifier. |
| `findUserByEmail(email)` | — | Retrieves a user by email. |
| `listAllUsers(adminId)` | Administrator | Lists every user. |

---

## WarehouseManagementUseCase

| Method | Allowed role | Description |
| :--- | :--- | :--- |
| `registerMarketplaceWarehouse(Warehouse, adminId)` | Administrator | Registers a Marketplace-owned warehouse. |
| `registerSellerWarehouse(Warehouse, sellerId, adminId)` | Administrator | Registers an additional warehouse for a Seller. |
| `findWarehouseById(id)` | — | Retrieves a warehouse. |
| `listMarketplaceWarehouses()` | — | Lists Marketplace warehouses. |
| `listWarehousesBySeller(sellerId)` | — | Lists the warehouses of a Seller. |
| `listAllWarehouses()` | — | Lists every warehouse. |

---

## ProductCatalogUseCase

| Method | Allowed role | Description |
| :--- | :--- | :--- |
| `createProduct(Product, sellerId)` | Seller | Registers a product in `SUSPENDED` status. |
| `updateProduct(productId, updatedProduct, sellerId)` | Owner Seller | Updates name, description, price, and variants. |
| `publishProduct(productId, sellerId)` | Owner Seller | Publishes the product (physical products need available stock). |
| `suspendProduct(productId, sellerId)` | Owner Seller | Temporarily hides the product. |
| `discontinueProduct(productId, sellerId)` | Owner Seller | Permanently discontinues the product. |
| `findProductById(id)` | — | Retrieves a product. |
| `listPublishedProducts()` | — | Public catalog. |
| `listProductsBySeller(sellerId)` | — | Products of a Seller. |

---

## InventoryManagementUseCase

Reservations, sale exits, and returns are **not** exposed here: they are triggered automatically by the order, shipment, and return flows so that stock can never be manipulated outside a business process.

| Method | Allowed role | Movement |
| :--- | :--- | :--- |
| `registerInventory(productId, warehouseId, initialQuantity, requesterId)` | Owner Seller / Logistics Operator | ENTRY |
| `addStock(productId, warehouseId, quantity, requesterId)` | Owner Seller / Logistics Operator | ENTRY |
| `adjustStock(productId, warehouseId, newQuantity, requesterId)` | Owner Seller / Logistics Operator | ADJUSTMENT |
| `markAsDamaged(productId, warehouseId, requesterId)` | Owner Seller / Logistics Operator | — |
| `markAsAvailable(productId, warehouseId, requesterId)` | Owner Seller / Logistics Operator | — |
| `checkAvailability(productId, warehouseId)` | — | Available units (0 if damaged). |
| `listInventoryByProduct(productId)` | — | Records of a product across warehouses. |
| `listInventoryByWarehouse(warehouseId)` | — | Records inside a warehouse. |
| `listMovements(productId, warehouseId)` | — | Movement history of a record. |

---

## ShoppingCartUseCase

All operations require an active Buyer with `commercialStatus = ENABLED`.

| Method | Description |
| :--- | :--- |
| `createCart(buyerId)` | Creates an empty cart. |
| `addProductToCart(buyerId, productId, quantity)` | Adds units of a published product. |
| `removeProductFromCart(buyerId, productId)` | Removes a product. |
| `updateProductQuantity(buyerId, productId, newQuantity)` | Changes the quantity of an item. |
| `getCartByBuyer(buyerId)` | Retrieves the cart. |
| `clearCart(buyerId)` | Empties the cart. |

---

## OrderProcessingUseCase

| Method | Allowed role | Description |
| :--- | :--- | :--- |
| `placeOrder(buyerId, deliveryAddress)` | Buyer | Converts the cart into a `PENDING_PAYMENT` order and reserves stock. Uses the main address when none is given. |
| `payOrder(orderId, buyerId)` | Owner Buyer | Moves the order to `PAID` and generates the Invoice. Digital-only orders become `DELIVERED`. |
| `cancelOrder(orderId, buyerId)` | Owner Buyer | Cancels a `PENDING_PAYMENT` order and releases the reserved stock. |
| `findOrderById(id)` | — | Retrieves an order. |
| `listOrdersByBuyer(buyerId)` | — | Orders of a Buyer. |
| `getInvoiceByOrder(orderId)` | — | Invoice of an order. |

---

## ShipmentManagementUseCase

| Method | Allowed role | Description |
| :--- | :--- | :--- |
| `createShipment(orderId, operatorId)` | Logistics Operator | Creates the shipment of a `PAID` physical order. |
| `dispatchShipment(trackingId, operatorId)` | Assigned Operator | Order → `DISPATCHED`; consumes reserved stock (SALE_EXIT). |
| `confirmDelivery(trackingId, operatorId)` | Assigned Operator | Order → `DELIVERED`. |
| `findShipmentByOrder(orderId)` | — | Shipment of an order. |
| `findShipmentByTrackingId(trackingId)` | — | Shipment by tracking id. |
| `listShipmentsByOperator(operatorId)` | — | Shipments of an operator. |

---

## ReturnRefundUseCase

| Method | Allowed role | Description |
| :--- | :--- | :--- |
| `requestReturn(orderId, buyerId, reason)` | Owner Buyer | Requests the return of a `DELIVERED` order. |
| `generateRefund(returnId, adminId)` | Administrator | Approves the return, restocks physical items (RETURN), and issues the refund. |
| `rejectReturn(returnId, adminId)` | Administrator | Rejects the return. |
| `findReturnById(returnId)` | — | Retrieves a return. |
| `listPendingReturns(adminId)` | Administrator | Returns awaiting resolution. |
| `findRefundById(refundId)` | — | Retrieves a refund. |
| `findRefundByReturn(returnId)` | — | Refund of a return. |

---

## MarketplaceReportUseCase

| Method | Allowed role | Description |
| :--- | :--- | :--- |
| `generateOperationalReport(requesterId)` | Supervisor / Administrator | Consolidated figures of users, products, orders, invoicing, refunds, and inventory. |
| `listOrdersByStatus(status, requesterId)` | Supervisor / Administrator | Operational follow-up of orders in a given state. |

---

## Design Rules

1. **Interface-driven design:** Every service exposes its capabilities through an Input Port.
2. **No infrastructure references:** Input Ports work with domain models only.
3. **Named use cases:** Method names describe the business action, not a CRUD verb.
4. **Explicit requester:** Whenever a use case requires role authorization, the requester's id is a parameter. Read operations marked "—" will be restricted by authentication at the API layer.
