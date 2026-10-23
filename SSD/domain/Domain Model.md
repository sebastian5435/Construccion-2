# Domain Model

## Introduction

The Domain Model represents the core business entities of the NexusMarket platform. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the functional specification.

The model follows Object-Oriented Design (OOD) and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, explicit object relationships and strongly-typed Value Objects are preferred over primitive types, and **entities own the rules that protect their own state** (rich domain model): an `Order` validates its own transitions, an `Inventory` refuses to go negative, a `Product` refuses to leave the `DISCONTINUED` state.

The model distinguishes between:
* **Users:** Authenticated participants authorized to interact with the Marketplace (Buyer, Seller, Logistics Operator, Administrator, Supervisor).
* **Products:** Physical or digital goods offered on the platform.
* **Inventory & Warehouses:** Distributed stock managed across physical storage locations, with a traceable movement history.
* **Commercial Lifecycle:** The flow from temporary selection (Shopping Cart) to confirmed commercial commitment (Order), its financial record (Invoice), logistical dispatch (Shipment), and post-sale operations (Return, Refund).
* **Administrative Information:** Consolidated read model for consultation (Operational Report).

---

## Domain Class Hierarchy

```text
User (Abstract)
├── Buyer
├── Seller
├── LogisticsOperator
├── Administrator
└── Supervisor

Product (Abstract)
├── PhysicalProduct
└── DigitalProduct

Warehouse
Inventory
InventoryMovement
ShoppingCart
OrderItem
Order
Invoice
Shipment
Return
Refund
OperationalReport
```

---

## Domain Relationships

```text
Administrator
 ├── registers ─────────────────> Seller (together with its first Warehouse)
 ├── registers ─────────────────> Warehouse
 └── resolves ──────────────────> Return ──generates──> Refund

Seller
 ├── owns ──────────────────────> Product
 └── owns ──────────────────────> Warehouse (type SELLER)

Warehouse
 └── stores ────────────────────> Inventory

Inventory
 ├── associated with ───────────> Product (PHYSICAL only)
 ├── stored in ─────────────────> Warehouse
 └── records ───────────────────> InventoryMovement

Buyer
 ├── owns ──────────────────────> ShoppingCart
 └── places ────────────────────> Order

ShoppingCart / Order
 └── contains ──────────────────> OrderItem ──refers to──> Product
                                          └──reserved in──> Warehouse

Order
 ├── generates ─────────────────> Invoice   (on payment)
 ├── generates ─────────────────> Shipment  (physical orders only)
 └── may generate ──────────────> Return

LogisticsOperator
 └── handles ───────────────────> Shipment

Supervisor
 └── consults ──────────────────> OperationalReport
```

---

## Core Entities

---

### User (Abstract)

#### Description
Represents any participant authorized to interact with the Marketplace. It centralizes shared identity and operational access information. Every user is assigned a single role. This class cannot be instantiated directly.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| identifier | String | Identity document of the user. **Unique.** |
| fullName | String | Official full name of the user. Not empty. |
| email | String | Primary email address for access and communication. **Unique.** |
| role | SystemRole | Single business role assigned to the user. |
| status | UserStatus | Current operational status of the user. |

#### Behavior
* `isActive()`: only active users can execute operations.
* `hasRole(role)`: used by the access guard to enforce RG-02 and RG-03.

---

### Buyer

#### Description
Represents a user who acquires products published on the Marketplace. Buyers can self-register and never manage information of other buyers or inventories.

#### Inherits From
`User`

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| mainAddress | String | Primary location used for deliveries. **Required.** |
| additionalAddresses | List\<String\> | Secondary delivery locations. Optional. |
| commercialStatus | CommercialStatus | Current commercial condition for making purchases. |

#### Behavior
* `canPurchase()`: the buyer is active and `ENABLED`.
* `hasAddress(address)`: an order can only be delivered to one of the buyer's registered addresses.

---

### Seller

#### Description
Represents a user responsible for registering and commercializing products. Sellers cannot self-register; an Administrator incorporates them together with their first warehouse.

#### Inherits From
`User`

---

### LogisticsOperator

#### Description
Represents the participant responsible for the physical operation of warehouses, inventory, and shipments.

#### Inherits From
`User`

---

### Administrator

#### Description
Represents the participant responsible for incorporating sellers, registering warehouses, managing users, and resolving returns and refunds.

#### Inherits From
`User`

---

### Supervisor

#### Description
Represents the consultation and operational monitoring profile. Supervisors can only read consolidated information.

#### Inherits From
`User`

---

### Product (Abstract)

#### Description
Represents goods offered in the catalog. This class cannot be instantiated directly; the concrete subclass determines the product type.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the product. |
| name | String | Commercial name. Required. |
| description | String | Commercial description. |
| price | double | Unit price. Cannot be negative. |
| seller | Seller | Seller who owns the product. |
| variants | List\<String\> | Differences in color, size, model, etc. |
| status | ProductStatus | Current publication status. |
| productType | ProductType | **Derived** from the subclass (abstract `getProductType()`). |

#### Behavior
* `publish()`, `suspend()`, `discontinue()`: lifecycle transitions; a discontinued product cannot be published, suspended, updated, or stocked.
* `isPhysical()`, `isPublished()`, `isOwnedBy(sellerId)`.

---

### PhysicalProduct

Tangible good that requires inventory, warehouse storage, and shipment. It needs available stock before it can be published. Inherits from `Product`.

---

### DigitalProduct

Intangible product delivered immediately after payment, without inventory or shipment. Inherits from `Product`.

---

### Warehouse

#### Description
Represents a physical storage location used for stock control.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the warehouse. |
| name | String | Name of the warehouse. Required. |
| address | String | Physical location. Required. |
| warehouseType | WarehouseType | `MARKETPLACE` or `SELLER`. |
| seller | Seller | Owner Seller. Always present for `SELLER` warehouses, never for `MARKETPLACE` ones. |

---

### Inventory

#### Description
Links a physical product to a specific warehouse and controls its stock. **Inventory can never be negative under any circumstance.**

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the record. |
| product | Product | Physical product being quantified. |
| warehouse | Warehouse | Location where the merchandise resides. |
| quantity | int | Physical units in the warehouse. Never negative. |
| reservedQuantity | int | Units reserved by pending or paid orders. |
| status | InventoryStatus | `AVAILABLE` or `DAMAGED`. |
| availableQuantity | int | Derived: `quantity − reservedQuantity`. |

#### Behavior
* `reserve(units)`: rejects damaged inventory and quantities above the available stock.
* `releaseReservation(units)`, `consumeReservation(units)`, `addStock(units)`, `adjustTo(newQuantity)`.
* `markAsDamaged()`: not allowed while there are pending reservations. `markAsAvailable()`.

#### Business Rules
- A `(product, warehouse)` pair has at most one inventory record.
- Only physical products can have inventory.
- A product can only be stocked in a Marketplace warehouse or in its own Seller's warehouse.

---

### InventoryMovement

#### Description
Immutable record of every stock movement, providing traceability of the distributed inventory.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier. |
| inventory | Inventory | Affected inventory record. |
| type | InventoryMovementType | Kind of movement. |
| quantity | int | Units moved. |
| performedBy | User | User who triggered the movement. |
| reference | String | Related order id or adjustment note. |
| occurredAt | LocalDateTime | Moment of the movement. |

---

### ShoppingCart

#### Description
Temporary selection of products made by a Buyer before confirming a purchase. A buyer has at most one cart.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier. |
| buyer | Buyer | Owner of the cart. |
| items | List\<OrderItem\> | Selected products, quantities, and unit prices. |

---

### OrderItem

#### Description
A line of a cart or an order.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| product | Product | Selected product. |
| quantity | int | Units. Greater than zero. |
| unitPrice | double | Price captured when the item was added. |
| sourceWarehouse | Warehouse | Warehouse where the units were reserved (physical items only). |
| subtotal | double | Derived: `unitPrice × quantity`. |

---

### Order

#### Description
Formal commercial commitment placed by a Buyer. It drives the central lifecycle of the platform.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier. |
| buyer | Buyer | Buyer who placed the order. |
| items | List\<OrderItem\> | Purchased products. |
| status | OrderStatus | Current state within the lifecycle. |
| deliveryAddress | String | One of the buyer's registered addresses. |
| totalAmount | double | Sum of the item subtotals. |
| createdAt / updatedAt | LocalDateTime | Audit timestamps. |

#### Behavior
* `markAsPendingPayment()`, `markAsPaid()`, `markAsDispatched()`, `markAsDelivered()`, `cancel()`: every transition is validated by the order itself. Invalid transitions raise `InvalidOrderStateTransitionException`; any change to a finalized order raises `OrderAlreadyFinalizedException`.
* `requiresShipping()`: true when at least one item is physical.

#### Lifecycle
```text
CART ──► PENDING_PAYMENT ──► PAID ──► DISPATCHED ──► DELIVERED
               │               └── digital-only ────────▲
               ▼
           CANCELLED
```

---

### Invoice

#### Description
Commercial document generated automatically when an order is paid.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Invoice identifier. |
| order | Order | Invoiced order. |
| totalAmount | double | Total settled amount. |
| issueDate | LocalDateTime | Issue date. |

---

### Shipment

#### Description
Logistics process that moves a paid physical order from the warehouse to the buyer.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| trackingId | String | Tracking or dispatch guide identifier. |
| order | Order | Shipped order. |
| logisticsOperator | LogisticsOperator | Operator assigned to the shipment. |
| destinationAddress | String | Delivery address taken from the order. |
| createdAt / dispatchedAt / deliveredAt | LocalDateTime | Logistics milestones. |

#### Behavior
* `dispatch()` and `confirmDelivery()` move the associated order to `DISPATCHED` and `DELIVERED`.
* `isAssignedTo(operatorId)`: only the assigned operator can operate the shipment.

---

### Return

#### Description
Post-sale process in which a Buyer returns a delivered order.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier. |
| order | Order | Returned order. Must be `DELIVERED`. |
| reason | String | Reason given by the buyer. Required. |
| status | ReturnStatus | `REQUESTED`, `APPROVED`, or `REJECTED`. |
| requestedAt / resolvedAt | LocalDateTime | Timestamps. |
| resolvedBy | Administrator | Administrator who resolved it. |

#### Behavior
* `approve(admin)` / `reject(admin)`: a return can only be resolved once.

---

### Refund

#### Description
Financial reimbursement produced when an Administrator approves a return.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier. |
| returnProcess | Return | Approved return. |
| amount | double | Refunded amount (order total). |
| issuedAt | LocalDateTime | Issue date. |

---

### OperationalReport

#### Description
Read model that consolidates administrative information for Supervisors and Administrators (OBJ-12). It is calculated on demand and is not persisted.

| Attribute | Type | Description |
| :--- | :--- | :--- |
| generatedAt | LocalDateTime | Generation moment. |
| usersByRole | Map\<SystemRole, Long\> | User count per role. |
| productsByStatus | Map\<ProductStatus, Long\> | Product count per status. |
| ordersByStatus | Map\<OrderStatus, Long\> | Order count per status. |
| totalInvoiced | double | Sum of all invoices. |
| totalRefunded | double | Sum of all refunds. |
| pendingReturns | long | Returns awaiting resolution. |
| damagedInventoryRecords | long | Inventory records marked as damaged. |

---

## Domain Design Rules

1. **Explicit Value Objects:** Controlled states, types, and roles use dedicated enums instead of strings.
2. **Object Associations:** Entities reference related domain objects directly (e.g., `LogisticsOperator` inside `Shipment`).
3. **Strict Specialization:** Abstract base classes (`User`, `Product`) enforce reuse and polymorphism; the product type is derived from the subclass, so it can never be inconsistent.
4. **Rich Entities:** Invariants that depend only on an entity's own state (inventory never negative, valid order transitions, discontinued products, single resolution of a return) are enforced inside the entity, not in the services.
