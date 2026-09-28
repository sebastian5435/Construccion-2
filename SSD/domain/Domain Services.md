# Domain Services

## Introduction

Domain Services encapsulate the business logic of the NexusMarket platform. They orchestrate the interaction between entities, enforce business rules, and expose the use cases defined in the functional specification.

The services follow the **Hexagonal Architecture** (Ports & Adapters) pattern:

* Each service **implements** an **Input Port** (a use-case interface) that defines what the system can do.
* Each service **depends** on one or more **Output Ports** (repository interfaces) that define what the system needs from the outside world.
* Services never depend on concrete infrastructure; they depend on abstractions.

This separation guarantees that business logic remains independent of persistence, transport, and framework details.

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
```

Each service is annotated with `@Service` and receives its dependencies through **constructor injection**, following the Dependency Inversion Principle.

---

## UserService

### Responsibility
Manages the full lifecycle of every user type: registration, activation, blocking, and lookup.

### Covers Objectives
`OBJ-01`, `OBJ-02`, `OBJ-03`

### Business Rules Enforced
* Email must be unique across the platform.
* Identifier (document) must be unique across the platform.
* Buyers may self-register. Sellers, Logistics Operators, Supervisors, and Administrators are **only** created by an active Administrator.
* Only Administrators can block or activate a user.
* The user performing an administrative action must have `status = ACTIVE`.

### Dependencies
`UserRepositoryPort`

---

## WarehouseService

### Responsibility
Handles the creation and administration of physical storage locations.

### Covers Objectives
`OBJ-04`

### Business Rules Enforced
* Marketplace warehouses can only be registered by an Administrator.
* Seller warehouses can only be registered by the Seller who owns them.
* A warehouse must have a name and an address.
* A Marketplace warehouse never has an associated Seller; a Seller warehouse always does.

### Dependencies
`WarehouseRepositoryPort`, `UserRepositoryPort`

---

## ProductService

### Responsibility
Manages the product catalog: creation, updates, and lifecycle transitions between `PUBLISHED`, `SUSPENDED`, and `DISCONTINUED`.

### Covers Objectives
`OBJ-05`

### Business Rules Enforced
* Only the Seller who created a product can modify its state.
* The price of a product cannot be negative.
* Discontinued products cannot be updated, republished, or suspended.
* Every product must have a name and a product type.

### Dependencies
`ProductRepositoryPort`, `UserRepositoryPort`

---

## InventoryService

### Responsibility
Manages distributed inventory across warehouses. Ensures the integrity of stock across the entire commercial cycle.

### Covers Objectives
`OBJ-06`

### Business Rules Enforced
* **Inventory can never be negative**, under any operation.
* An inventory record must be linked to an existing Product and an existing Warehouse.
* An `(product, warehouse)` pair can only have one inventory record.
* You cannot reserve more units than the current available quantity.
* You cannot release or consume more units than the currently reserved quantity.
* An adjusted quantity cannot be lower than the reserved quantity.

### Dependencies
`InventoryRepositoryPort`, `ProductRepositoryPort`, `WarehouseRepositoryPort`

---

## ShoppingCartService

### Responsibility
Manages the temporary product selection that a Buyer prepares before confirming a purchase.

### Covers Objectives
`OBJ-07`

### Business Rules Enforced
* Only an active Buyer with `commercialStatus = ENABLED` can operate a cart.
* A Buyer has at most one active shopping cart at a time.
* Only products with `status = PUBLISHED` can be added to a cart.
* A quantity must be greater than zero.

### Dependencies
`ShoppingCartRepositoryPort`, `UserRepositoryPort`, `ProductRepositoryPort`

---

## OrderProcessingService

### Responsibility
Central transactional service. Orchestrates the complete order lifecycle and generates the associated invoice.

### Covers Objectives
`OBJ-08`, `OBJ-09`

### Business Rules Enforced
* An order cannot be placed from an empty shopping cart.
* Order transitions follow the strict sequence:
  `CART → PENDING_PAYMENT → PAID → DISPATCHED → DELIVERED`.
  Any other transition raises `InvalidOrderStateTransitionException`.
* Once an order reaches `DELIVERED` it is finalized and cannot be modified.
* Paying an order automatically generates an `Invoice`.
* Only a Logistics Operator can dispatch or deliver an order.
* Only the Buyer who placed an order can cancel it, and only if it has not been dispatched.

### Dependencies
`OrderRepositoryPort`, `InvoiceRepositoryPort`, `ShoppingCartRepositoryPort`, `UserRepositoryPort`

---

## ShipmentService

### Responsibility
Manages the logistics dispatch of paid orders.

### Covers Objectives
`OBJ-10`

### Business Rules Enforced
* A shipment can only be created for an order in `PAID` status.
* An order can have at most one associated shipment.
* Only an active Logistics Operator can create a shipment.

### Dependencies
`ShipmentRepositoryPort`, `OrderRepositoryPort`, `UserRepositoryPort`

---

## ReturnRefundService

### Responsibility
Manages the post-sale reverse flow: returns of delivered products and their associated refunds.

### Covers Objectives
`OBJ-11`

### Business Rules Enforced
* A return can only be requested for an order in `DELIVERED` status.
* Only the Buyer who placed the order can request its return.
* Only one return process may exist per order.
* Only an Administrator can generate a refund.
* Only one refund may exist per return process.

### Dependencies
`ReturnRepositoryPort`, `RefundRepositoryPort`, `OrderRepositoryPort`, `UserRepositoryPort`

---

## Design Rules

1. **Single Responsibility:** Each service handles one bounded business capability.
2. **Dependency Inversion:** Services depend on Output Port interfaces, never on concrete adapters.
3. **Explicit Business Rules:** Every rule from the functional specification is enforced by an explicit check that throws a specific `DomainException`.
4. **Immutable Transitions:** State transitions (Order, Product, User) are validated before being applied.
5. **Constructor Injection:** All dependencies are declared final and injected through the constructor.
