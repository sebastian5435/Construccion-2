# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain[cite: 3, 4].

Unlike Entities, Value Objects do not have their own identity[cite: 3, 4]. They are defined entirely by their values and encapsulate controlled business concepts, improving domain expressiveness and preventing primitive obsession throughout the application[cite: 3, 4].

The NexusMarket domain uses Value Objects for roles, operational statuses, product classifications, warehouse origins, and order lifecycles[cite: 4, 5].

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

Represents the abstract base structure for business catalogs within the domain[cite: 3, 4].

## Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| code | String | Unique business identifier of the catalog value[cite: 3, 4]. |
| name | String | Human-readable name displayed within the application[cite: 3, 4]. |
| description | String | Business definition of the catalog value[cite: 3, 4]. |

## Characteristics

* Immutable[cite: 3, 5].
* Equality is determined by value rather than object identity[cite: 3, 5].
* Eliminates the use of raw strings across the codebase[cite: 3, 5].

---

# SystemRole

## Description

Defines the responsibilities and operational capabilities assigned to a user within NexusMarket[cite: 3, 4].

## Inherits From
`DomainCatalog`[cite: 3, 4]

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| BUYER | Buyer | Person who purchases published products[cite: 4]. |
| SELLER | Seller | Participant responsible for registering and managing products[cite: 4]. |
| LOGISTICS_OPERATOR | Logistics Operator | Participant in charge of warehouse operations and dispatches[cite: 4]. |
| ADMINISTRATOR | Administrator | Participant responsible for managing sellers and warehouses[cite: 4]. |
| SUPERVISOR | Supervisor | Consultation and operational monitoring profile[cite: 4]. |

---

# UserStatus

## Description

Represents the general operational access status of a user in the system[cite: 3, 4].

## Inherits From
`DomainCatalog`[cite: 3, 4]

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| ACTIVE | Active | User can access and operate within the system normally[cite: 3, 4]. |
| BLOCKED | Blocked | User access has been suspended[cite: 3, 4]. |

---

# CommercialStatus

## Description

Represents the commercial eligibility of a Buyer to execute purchase transactions[cite: 4].

## Inherits From
`DomainCatalog`[cite: 4]

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| ENABLED | Enabled | Buyer is authorized to make purchases[cite: 4]. |
| RESTRICTED | Restricted | Buyer has commercial restrictions applied[cite: 4]. |

---

# ProductType

## Description

Classifies the logistical nature of goods offered in the catalog[cite: 4].

## Inherits From
`DomainCatalog`[cite: 4]

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| PHYSICAL | Physical | Tangible good requiring warehouse inventory and shipping[cite: 4]. |
| DIGITAL | Digital | Intangible good delivered immediately upon payment[cite: 4]. |

---

# ProductStatus

## Description

Represents the catalog visibility and lifecycle state of a product[cite: 4].

## Inherits From
`DomainCatalog`[cite: 4]

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| PUBLISHED | Published | Product is active and available for purchase[cite: 4]. |
| SUSPENDED | Suspended | Product is temporarily hidden from the catalog[cite: 4]. |
| DISCONTINUED | Discontinued | Product is permanently removed from the catalog[cite: 4]. |

---

# WarehouseType

## Description

Classifies the physical storage infrastructure origin[cite: 4].

## Inherits From
`DomainCatalog`[cite: 4]

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| MARKETPLACE | Marketplace Warehouse | Centralized storage managed by NexusMarket[cite: 4]. |
| SELLER | Seller Warehouse | External storage managed directly by a seller[cite: 4]. |

---

# OrderStatus

## Description

Represents the sequential states of an order throughout its business lifecycle[cite: 4].

## Inherits From
`DomainCatalog`[cite: 4]

## Allowed Values

| Code | Name | Description |
| :--- | :--- | :--- |
| CART | Cart | Provisional selection of items[cite: 4]. |
| PENDING_PAYMENT | Pending Payment | Awaiting financial transaction confirmation[cite: 4]. |
| PAID | Paid | Payment confirmed; order preparation initiated[cite: 4]. |
| DISPATCHED | Dispatched | Physical departure from warehouse in transit[cite: 4]. |
| DELIVERED | Delivered | Final handover to the buyer successfully completed[cite: 4]. |

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

1. **Immutability:** Value Objects cannot be altered once created[cite: 3, 5].
2. **Type Safety:** Entities must declare fields using these specific types (e.g., `UserStatus status` instead of `String status`)[cite: 3, 5].
3. **Controlled Scope:** Enforces business consistency across all domain layers[cite: 3, 5].