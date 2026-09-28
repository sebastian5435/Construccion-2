# Input Ports

## Introduction

Input Ports are the entry point of the domain. They are **interfaces** that describe *what the system can do* without saying *how* it does it or *who* calls it.

Following the Hexagonal Architecture pattern:

* Every use case defined in the functional specification is expressed as a method on an Input Port.
* Domain Services implement these interfaces and contain the business logic.
* External adapters (REST controllers, message consumers, CLI, tests) can invoke the system by depending only on the Input Ports, never on the concrete services.

This design keeps the domain free from framework and delivery-mechanism concerns.

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
```

---

## UserManagementUseCase

Manages user registration, activation, and consultation.

| Method | Description |
| :--- | :--- |
| `registerBuyer(Buyer)` | Self-registration of a Buyer. |
| `registerSeller(Seller, adminId)` | Administrator registers a new Seller. |
| `registerLogisticsOperator(LogisticsOperator, adminId)` | Administrator registers a new Logistics Operator. |
| `registerSupervisor(Supervisor, adminId)` | Administrator registers a new Supervisor. |
| `registerAdministrator(Administrator, requesterAdminId)` | An existing Administrator registers another Administrator. |
| `blockUser(userId, adminId)` | Administrator blocks a user. |
| `activateUser(userId, adminId)` | Administrator reactivates a blocked user. |
| `findUserById(id)` | Retrieves a user by identifier. |
| `findUserByEmail(email)` | Retrieves a user by email. |
| `listAllUsers(adminId)` | Administrator lists every user. |

---

## WarehouseManagementUseCase

Manages the physical storage locations of the platform.

| Method | Description |
| :--- | :--- |
| `registerMarketplaceWarehouse(Warehouse, adminId)` | Administrator registers a Marketplace-owned warehouse. |
| `registerSellerWarehouse(Warehouse, sellerId)` | Seller registers a warehouse under their ownership. |
| `findWarehouseById(id)` | Retrieves a warehouse by identifier. |
| `listMarketplaceWarehouses()` | Lists all warehouses owned by the Marketplace. |
| `listWarehousesBySeller(sellerId)` | Lists all warehouses of a given Seller. |
| `listAllWarehouses()` | Lists every warehouse. |

---

## ProductCatalogUseCase

Manages the product catalog and its lifecycle.

| Method | Description |
| :--- | :--- |
| `createProduct(Product, sellerId)` | Seller registers a new product. |
| `updateProduct(productId, updatedProduct, sellerId)` | Seller updates one of their products. |
| `publishProduct(productId, sellerId)` | Seller marks the product as PUBLISHED. |
| `suspendProduct(productId, sellerId)` | Seller marks the product as SUSPENDED. |
| `discontinueProduct(productId, sellerId)` | Seller permanently discontinues the product. |
| `findProductById(id)` | Retrieves a product by identifier. |
| `listPublishedProducts()` | Lists every product currently on sale. |
| `listProductsBySeller(sellerId)` | Lists all products of a Seller. |

---

## InventoryManagementUseCase

Manages distributed inventory operations.

| Method | Description |
| :--- | :--- |
| `registerInventory(productId, warehouseId, initialQuantity)` | Creates the inventory record for a product in a warehouse. |
| `addStock(productId, warehouseId, quantity)` | Movement type: ENTRY. |
| `reserveStock(productId, warehouseId, quantity)` | Movement type: RESERVATION. |
| `releaseReservedStock(productId, warehouseId, quantity)` | Releases a previous reservation without consuming stock. |
| `consumeReservedStock(productId, warehouseId, quantity)` | Movement type: SALE_EXIT. Deducts previously reserved stock. |
| `adjustStock(productId, warehouseId, newQuantity)` | Movement type: ADJUSTMENT. |
| `returnStock(productId, warehouseId, quantity)` | Movement type: RETURN. |
| `checkAvailability(productId, warehouseId)` | Returns the currently available quantity (quantity − reserved). |
| `listInventoryByProduct(productId)` | Lists all inventory records for a product across warehouses. |
| `listInventoryByWarehouse(warehouseId)` | Lists all inventory records inside a warehouse. |

---

## ShoppingCartUseCase

Manages the temporary product selection before checkout.

| Method | Description |
| :--- | :--- |
| `createCart(buyerId)` | Creates an empty cart for the Buyer. |
| `addProductToCart(buyerId, productId, quantity)` | Adds units of a product to the Buyer's cart. |
| `removeProductFromCart(buyerId, productId)` | Removes a product from the cart. |
| `updateProductQuantity(buyerId, productId, newQuantity)` | Changes the quantity of an existing item. |
| `getCartByBuyer(buyerId)` | Retrieves the Buyer's current cart. |
| `clearCart(buyerId)` | Empties the cart. |

---

## OrderProcessingUseCase

Manages the complete order lifecycle and invoice generation.

| Method | Description |
| :--- | :--- |
| `placeOrder(buyerId)` | Converts the Buyer's cart into a `PENDING_PAYMENT` order. |
| `payOrder(orderId)` | Transitions the order to `PAID` and generates the Invoice. |
| `dispatchOrder(orderId, operatorId)` | Logistics Operator moves the order to `DISPATCHED`. |
| `deliverOrder(orderId, operatorId)` | Logistics Operator moves the order to `DELIVERED`. |
| `cancelOrder(orderId, buyerId)` | Buyer cancels the order (only if not dispatched). |
| `findOrderById(id)` | Retrieves an order by identifier. |
| `listOrdersByBuyer(buyerId)` | Lists all orders of a Buyer. |
| `getInvoiceByOrder(orderId)` | Retrieves the invoice associated with an order. |

---

## ShipmentManagementUseCase

Manages the logistics dispatch of paid orders.

| Method | Description |
| :--- | :--- |
| `createShipment(orderId, operatorId)` | Creates a shipment for an order in `PAID` status. |
| `findShipmentByOrder(orderId)` | Retrieves the shipment of an order. |
| `findShipmentByTrackingId(trackingId)` | Retrieves a shipment by its tracking id. |
| `listShipmentsByOperator(operatorId)` | Lists all shipments handled by a Logistics Operator. |

---

## ReturnRefundUseCase

Manages the post-sale reverse flow.

| Method | Description |
| :--- | :--- |
| `requestReturn(orderId, buyerId)` | Buyer requests the return of a `DELIVERED` order. |
| `generateRefund(returnId, adminId)` | Administrator generates the refund associated with a return. |
| `findReturnById(returnId)` | Retrieves a return process by identifier. |
| `findRefundById(refundId)` | Retrieves a refund by identifier. |
| `findRefundByReturn(returnId)` | Retrieves the refund associated with a return. |

---

## Design Rules

1. **Interface-driven design:** Every service exposes its capabilities through an Input Port.
2. **No infrastructure references:** Input Ports work with domain models only; no framework classes, DTOs, or transport annotations leak in.
3. **Named use cases:** Method names describe the business action, not a CRUD verb.
4. **Explicit authorization arguments:** Whenever a use case requires role authorization, the requester's id is a required parameter.
