# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain.

Unlike Entities, Value Objects do not have their own identity. They are defined entirely by their values and encapsulate controlled business concepts, improving domain expressiveness and preventing primitive obsession throughout the application.

The NexusMarket domain implements its Value Objects as Java `enum` types located in `application.domain.valueobjects`. Enums are immutable, compared by value, and restrict every field to a closed business catalog. Where a catalog carries business rules (for example, the order lifecycle), the rule lives inside the enum itself.

---

# Value Object Inventory

```text
SystemRole
UserStatus
CommercialStatus
ProductType
ProductStatus
WarehouseType
InventoryStatus
InventoryMovementType
OrderStatus
ReturnStatus
```

---

# SystemRole

Defines the responsibilities and operational capabilities assigned to a user. Every user has exactly one role (RG-02).

| Code | Description |
| :--- | :--- |
| BUYER | Person who purchases published products. |
| SELLER | Participant responsible for registering and managing products. |
| LOGISTICS_OPERATOR | Participant in charge of warehouse operations and dispatches. |
| ADMINISTRATOR | Participant responsible for managing sellers, warehouses, and refunds. |
| SUPERVISOR | Consultation and operational monitoring profile. |

---

# UserStatus

Represents the general operational access status of a user. Only `ACTIVE` users can execute operations.

| Code | Description |
| :--- | :--- |
| ACTIVE | User can access and operate within the system normally. |
| BLOCKED | User access has been suspended by an Administrator. |

---

# CommercialStatus

Represents the commercial eligibility of a Buyer to execute purchase transactions.

| Code | Description |
| :--- | :--- |
| ENABLED | Buyer is authorized to make purchases. |
| RESTRICTED | Buyer has commercial restrictions applied and cannot operate a cart or place orders. |

---

# ProductType

Classifies the logistical nature of goods offered in the catalog. It is **derived from the product subclass** (`PhysicalProduct` → `PHYSICAL`, `DigitalProduct` → `DIGITAL`) and cannot be changed after registration.

| Code | Description |
| :--- | :--- |
| PHYSICAL | Tangible good requiring warehouse inventory and shipping. |
| DIGITAL | Intangible good delivered immediately upon payment, without inventory. |

---

# ProductStatus

Represents the catalog visibility and lifecycle state of a product.

| Code | Description |
| :--- | :--- |
| PUBLISHED | Product is visible and available for purchase. |
| SUSPENDED | Product is temporarily hidden from the catalog. Every new product starts here. |
| DISCONTINUED | Product is permanently removed from the catalog. Terminal state. |

## Lifecycle

```text
SUSPENDED ⇄ PUBLISHED
    │           │
    └─────┬─────┘
          ▼
    DISCONTINUED
```

---

# WarehouseType

Classifies the physical storage infrastructure origin.

| Code | Description |
| :--- | :--- |
| MARKETPLACE | Centralized storage managed by NexusMarket. Never has an owner Seller. |
| SELLER | External storage owned by a specific Seller. |

---

# InventoryStatus

Represents the physical condition of an inventory record.

| Code | Description |
| :--- | :--- |
| AVAILABLE | Stock can be reserved for orders. |
| DAMAGED | Stock cannot be reserved under any circumstance (Validation 11). |

---

# InventoryMovementType

Classifies every stock movement recorded for traceability.

| Code | Business name | Trigger |
| :--- | :--- | :--- |
| ENTRY | Ingreso | Inventory registration or manual stock addition. |
| RESERVATION | Reserva | A Buyer places an order. |
| RESERVATION_RELEASE | Liberación de reserva | A pending order is cancelled. |
| SALE_EXIT | Salida por venta | A shipment is dispatched. |
| ADJUSTMENT | Ajuste | Manual correction of the physical count. |
| RETURN | Devolución | An approved return puts units back in stock. |

---

# OrderStatus

Represents the sequential states of an order throughout its business lifecycle. The enum exposes `canTransitionTo(target)` and `isFinal()` so the lifecycle rules live in a single place.

| Code | Description |
| :--- | :--- |
| CART | Provisional selection of items while the order is being assembled. |
| PENDING_PAYMENT | Stock reserved; awaiting financial confirmation. |
| PAID | Payment confirmed; invoice generated; preparation initiated. |
| DISPATCHED | Physical departure from the warehouse. |
| DELIVERED | Final handover to the buyer. Terminal state. |
| CANCELLED | Order cancelled by the buyer before payment. Terminal state. |

## Lifecycle

```text
CART ──► PENDING_PAYMENT ──► PAID ──► DISPATCHED ──► DELIVERED
               │               │                        ▲
               ▼               └── digital-only order ──┘
           CANCELLED
```

* `DELIVERED` and `CANCELLED` are final: the order cannot be modified (Validation 11).
* `PAID → DELIVERED` is only allowed when the order contains digital products exclusively.

---

# ReturnStatus

Represents the resolution state of a post-sale return.

| Code | Description |
| :--- | :--- |
| REQUESTED | Buyer requested the return; awaiting an Administrator. |
| APPROVED | Administrator approved the return and generated the refund. |
| REJECTED | Administrator rejected the return. |

---

# Design Rules

1. **Immutability:** Value Objects cannot be altered once created.
2. **Type Safety:** Entities declare fields using these types (e.g., `UserStatus status` instead of `String status`).
3. **Behavior close to data:** Catalog-level rules (such as valid order transitions) are implemented inside the enum.
