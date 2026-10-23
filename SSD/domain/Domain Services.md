# Domain Services

## Introduction

Domain Services encapsulate the business logic of the NexusMarket platform that does not belong to a single entity. They orchestrate entities, enforce cross-entity business rules, and expose the use cases defined in the functional specification.

The services follow the **Hexagonal Architecture** (Ports & Adapters) pattern:

* Each use-case service **implements** an **Input Port**.
* Each service **depends** on **Output Ports** (repository interfaces), never on concrete adapters.
* Rules that only involve one entity's state live in the entity itself (see *Domain Model*); services coordinate.

---

## Service Inventory

```text
UserService                 → UserManagementUseCase
WarehouseService            → WarehouseManagementUseCase
ProductService              → ProductCatalogUseCase
InventoryService            → InventoryManagementUseCase
ShoppingCartService         → ShoppingCartUseCase
OrderProcessingService      → OrderProcessingUseCase
ShipmentService             → ShipmentManagementUseCase
ReturnRefundService         → ReturnRefundUseCase
MarketplaceReportService    → MarketplaceReportUseCase

AccessGuard                 (internal: role and status authorization)
StockMovementService        (internal: stock flow of orders + movement history)
```

Every class receives its dependencies through **constructor injection**.

---

## Internal Domain Services

### AccessGuard

Single place where RG-01, RG-02, and RG-03 are enforced. `requireActiveUser(userId, action, allowedRoles...)` verifies that the user exists, has one of the allowed roles, and is `ACTIVE`; otherwise it raises `EntityNotFoundException`, `UnauthorizedRoleException`, or `InvalidOperationException`.

### StockMovementService

Connects the inventory with the commercial lifecycle and records every `InventoryMovement`.

| Operation | Triggered by | Movement |
| :--- | :--- | :--- |
| `reserveForOrder(order, user)` | `placeOrder` | RESERVATION |
| `releaseForOrder(order, user)` | `cancelOrder` | RESERVATION_RELEASE |
| `consumeForOrder(order, user)` | `dispatchShipment` | SALE_EXIT |
| `restockForOrder(order, user)` | `generateRefund` | RETURN |

Reservation picks, for each physical item, the first non-damaged warehouse with enough available units. All items are allocated before any stock is touched, so a failure leaves the inventory unchanged. Digital items are ignored.

---

## UserService

**Covers:** `OBJ-01`, `OBJ-02`, `OBJ-03` · **Depends on:** `UserRepositoryPort`, `WarehouseRepositoryPort`, `AccessGuard`

* Email and identity document are unique across the platform.
* Buyers self-register and must provide a main address; they start `ACTIVE` and `ENABLED`.
* Sellers, Logistics Operators, Supervisors, and Administrators are created only by an active Administrator.
* A Seller is always registered together with their first warehouse (flow step "Incorporación").
* Only Administrators can block or activate users, and an Administrator cannot block themselves.

---

## WarehouseService

**Covers:** `OBJ-04` · **Depends on:** `WarehouseRepositoryPort`, `UserRepositoryPort`, `AccessGuard`

* Only Administrators register warehouses, both Marketplace and Seller ones.
* A warehouse requires a name and an address.
* A Marketplace warehouse never has a Seller; a Seller warehouse always does.

---

## ProductService

**Covers:** `OBJ-05` · **Depends on:** `ProductRepositoryPort`, `InventoryRepositoryPort`, `AccessGuard`

* Only active Sellers create products, and only the owner can modify them.
* New products start `SUSPENDED` (flow: catalog → inventory → publication).
* A physical product needs available, non-damaged stock before it can be published.
* Name is required, price cannot be negative, and the product type cannot change.
* Discontinued products cannot be updated, published, or suspended.

---

## InventoryService

**Covers:** `OBJ-06` · **Depends on:** `InventoryRepositoryPort`, `InventoryMovementRepositoryPort`, `ProductRepositoryPort`, `WarehouseRepositoryPort`, `StockMovementService`, `AccessGuard`

* Only Logistics Operators and the owner Seller can manage a product's inventory.
* Only physical, non-discontinued products can be stocked.
* A product can only be stocked in a Marketplace warehouse or in its own Seller's warehouse.
* One inventory record per `(product, warehouse)`.
* Inventory is never negative; adjusted quantities cannot go below the reserved quantity.
* Damaged inventory reports 0 available units and cannot be reserved.
* Every manual change records an `InventoryMovement`.

---

## ShoppingCartService

**Covers:** `OBJ-07` · **Depends on:** `ShoppingCartRepositoryPort`, `ProductRepositoryPort`, `AccessGuard`

* Only an active Buyer with `commercialStatus = ENABLED` can operate a cart.
* A Buyer has at most one cart.
* Only `PUBLISHED` products can be added; quantities must be greater than zero.
* The unit price is captured when the product is added.

---

## OrderProcessingService

**Covers:** `OBJ-08`, `OBJ-09` · **Depends on:** `OrderRepositoryPort`, `InvoiceRepositoryPort`, `ShoppingCartRepositoryPort`, `StockMovementService`, `AccessGuard`

* An order cannot be placed from an empty cart, and every product must still be published.
* The delivery address must be one of the buyer's addresses (main address by default).
* Placing an order reserves stock for every physical item; if any item lacks stock, nothing is reserved.
* Only the Buyer who placed the order can pay or cancel it.
* Paying generates the `Invoice` automatically. Digital-only orders are delivered immediately.
* Only `PENDING_PAYMENT` orders can be cancelled; cancellation releases the reservation. Paid orders follow the return/refund flow.
* `DELIVERED` and `CANCELLED` orders cannot be modified.

---

## ShipmentService

**Covers:** `OBJ-10` · **Depends on:** `ShipmentRepositoryPort`, `OrderRepositoryPort`, `StockMovementService`, `AccessGuard`

* Only active Logistics Operators create shipments, and only for `PAID` orders with physical products.
* An order has at most one shipment; its destination is the order's delivery address.
* Only the operator assigned to the shipment can dispatch it or confirm its delivery.
* Dispatching consumes the reserved stock (SALE_EXIT).

---

## ReturnRefundService

**Covers:** `OBJ-11` · **Depends on:** `ReturnRepositoryPort`, `RefundRepositoryPort`, `OrderRepositoryPort`, `StockMovementService`, `AccessGuard`

* Only the Buyer who placed a `DELIVERED` order can request its return, with a reason, once per order.
* Only Administrators resolve returns, and each return is resolved once.
* Approving a return (`generateRefund`) puts physical units back in their source warehouse (RETURN) and issues a single refund for the order total.

---

## MarketplaceReportService

**Covers:** `OBJ-12` · **Depends on:** all read repositories, `AccessGuard`

* Only Supervisors and Administrators can consult reports.
* The operational report is computed on demand from the current data.

---

## Design Rules

1. **Single Responsibility:** Each service handles one business capability.
2. **Dependency Inversion:** Services depend on Output Port interfaces, never on adapters.
3. **Explicit Business Rules:** Every rule throws a specific `DomainException`.
4. **Rich Entities, thin services:** State transitions and invariants are validated by the entities; services handle authorization, lookups, and coordination.
5. **No duplicated cross-cutting logic:** Authorization lives in `AccessGuard` and stock flow in `StockMovementService`.
