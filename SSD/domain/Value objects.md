# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain.

Unlike Entities, Value Objects do not have their own identity. They are defined entirely by their values and encapsulate controlled business concepts, improving domain expressiveness and preventing primitive obsession throughout the application.

The NexusMarket domain uses Value Objects for roles, operational statuses, product classifications, warehouse origins, and order lifecycles.

---

# Value Object Hierarchy

```text
DomainCatalog (Abstract)
├── SystemRole
├── UserStatus
├── CommercialStatus
├── ProductType
├── ProductStatus
├── WarehouseType
└── OrderStatus
```

---

# DomainCatalog (Abstract)

## Description

Represents the abstract base structure for business catalogs within the domain.

## Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| code | String | Unique business identifier of the catalog value. |
| name | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value. |

## Characteristics

* Immutable.
* Equality is determined by value rather than object identity.
* Eliminates the use of raw strings across the codebase.

---

# SystemRole

## Description

Defines the responsibilities and operational capabilities assigned to a user within NexusMarket.

## Inherits From
`DomainCatalog`

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| BUYER | Buyer | Person who purchases published products. |
| SELLER | Seller | Participant responsible for registering and managing products. |
| LOGISTICS_OPERATOR | Logistics Operator | Participant in charge of warehouse operations and dispatches. |
| ADMINISTRATOR | Administrator | Participant responsible for managing sellers and warehouses. |
| SUPERVISOR | Supervisor | Consultation and operational monitoring profile. |

---

# UserStatus

## Description

Represents the general operational access status of a user in the system.

## Inherits From
`DomainCatalog`

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| ACTIVE | Active | User can access and operate within the system normally. |
| BLOCKED | Blocked | User access has been suspended. |

---

# CommercialStatus

## Description

Represents the commercial eligibility of a Buyer to execute purchase transactions.

## Inherits From
`DomainCatalog`

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| ENABLED | Enabled | Buyer is authorized to make purchases. |
| RESTRICTED | Restricted | Buyer has commercial restrictions applied. |

---

# ProductType

## Description

Classifies the logistical nature of goods offered in the catalog.

## Inherits From
`DomainCatalog`

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| PHYSICAL | Physical | Tangible good requiring warehouse inventory and shipping. |
| DIGITAL | Digital | Intangible good delivered immediately upon payment. |

---

# ProductStatus

## Description

Represents the catalog visibility and lifecycle state of a product.

## Inherits From
`DomainCatalog`

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| PUBLISHED | Published | Product is active and available for purchase. |
| SUSPENDED | Suspended | Product is temporarily hidden from the catalog. |
| DISCONTINUED | Discontinued | Product is permanently removed from the catalog. |

---

# WarehouseType

## Description

Classifies the physical storage infrastructure origin.

## Inherits From
`DomainCatalog`

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| MARKETPLACE | Marketplace Warehouse | Centralized storage managed by NexusMarket. |
| SELLER | Seller Warehouse | External storage managed directly by a seller. |

---

# OrderStatus

## Description

Represents the sequential states of an order throughout its business lifecycle.

## Inherits From
`DomainCatalog`

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| CART | Cart | Provisional selection of items. |
| PENDING_PAYMENT | Pending Payment | Awaiting financial transaction confirmation. |
| PAID | Paid | Payment confirmed; order preparation initiated. |
| DISPATCHED | Dispatched | Physical departure from warehouse in transit. |
| DELIVERED | Delivered | Final handover to the buyer successfully completed. |

## Lifecycle

```text
CART
 │
 ▼
PENDING_PAYMENT
 │
 ▼
PAID
 │
 ▼
DISPATCHED
 │
 ▼
DELIVERED
```

---

# Design Rules

1. **Immutability:** Value Objects cannot be altered once created.
2. **Type Safety:** Entities must declare fields using these specific types (e.g., `UserStatus status` instead of `String status`).
3. **Controlled Scope:** Enforces business consistency across all domain layers.