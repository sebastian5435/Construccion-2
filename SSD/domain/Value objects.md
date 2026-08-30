# Domain Value Objects

## Introduction

Value Objects represent immutable concepts within the NexusMarket domain.

Unlike Entities, Value Objects do not have their own identity. They are defined entirely by their values and are used to encapsulate controlled business concepts, improve domain expressiveness, and prevent the use of primitive values or scattered string literals throughout the application.

The NexusMarket domain uses Value Objects for business catalogs such as roles, statuses, product types, warehouse types, and order lifecycles.

All business catalogs inherit from `DomainCatalog`.

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

Represents a generic business catalog used throughout the NexusMarket domain.

`DomainCatalog` provides a consistent structure for controlled business values that require a code, human-readable name, and business description.

This class cannot be instantiated directly.

## Attributes

| Attribute   | Type   | Description                                           |
| ----------- | ------ | ----------------------------------------------------- |
| code        | String | Unique business identifier of the catalog value.      |
| name        | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value.             |

## Characteristics

* Immutable.
* Equality is determined by value rather than object identity.
* Catalog values are controlled by the domain.
* Catalog values must not be represented by arbitrary strings throughout the application.
* Each catalog value must have a unique `code`.

---

# SystemRole

## Description

Represents the responsibilities and permissions assigned to a participant within the Marketplace.

The role is a characteristic of `User` because it represents what the person means within the system and the responsibilities associated with that participant. 

## Inherits From

`DomainCatalog`

## Allowed Values

| Code               | Name               | Description                                                                 |
| ------------------ | ------------------ | --------------------------------------------------------------------------- |
| BUYER              | Buyer              | User who acquires published products.                                       |
| SELLER             | Seller             | Participant responsible for registering and managing products.              |
| LOGISTICS_OPERATOR | Logistics Operator | Participant in charge of physical warehouse and dispatch operations.        |
| ADMINISTRATOR      | Administrator      | Participant responsible for managing sellers and warehouses.                |
| SUPERVISOR         | Supervisor         | Participant responsible for consultation and operational tracking.          |

---

# UserStatus

## Description

Represents the current operational status of a general user within the platform.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                           |
| -------- | -------- | ----------------------------------------------------- |
| ACTIVE   | Active   | User can access the system normally.                  |
| BLOCKED  | Blocked  | User access has been suspended due to policy reasons. |

---

# CommercialStatus

## Description

Represents the commercial condition of a Buyer regarding their ability to execute purchases on the platform.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code       | Name       | Description                                        |
| ---------- | ---------- | -------------------------------------------------- |
| ENABLED    | Enabled    | Buyer is fully allowed to make purchases.          |
| RESTRICTED | Restricted | Buyer has temporary commercial restrictions applied. |

---

# ProductType

## Description

Classifies the nature of the goods offered in the catalog, determining if they require physical logistics.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code     | Name     | Description                                               |
| -------- | -------- | --------------------------------------------------------- |
| PHYSICAL | Physical | Tangible good requiring inventory and physical dispatch.  |
| DIGITAL  | Digital  | Intangible good allowing immediate delivery post-payment. |

---

# ProductStatus

## Description

Represents the current visibility and availability lifecycle of a product in the catalog.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code         | Name         | Description                                        |
| ------------ | ------------ | -------------------------------------------------- |
| PUBLISHED    | Published    | Product is visible and available for purchase.     |
| SUSPENDED    | Suspended    | Product is temporarily hidden from the catalog.    |
| DISCONTINUED | Discontinued | Product is permanently removed from offerings.     |

---

# WarehouseType

## Description

Classifies the operational nature of the physical storage locations.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code        | Name        | Description                                          |
| ----------- | ----------- | ---------------------------------------------------- |
| MARKETPLACE | Marketplace | Centralized warehouse managed by NexusMarket.        |
| SELLER      | Seller      | External warehouse managed directly by a Seller.     |

---

# OrderStatus

## Description

Represents the central lifecycle of a commercial transaction within the Marketplace.

The status changes as the order moves through selection, payment, logistics preparation, dispatch, and final delivery.

## Inherits From

`DomainCatalog`

## Allowed Values

| Code            | Name            | Description                                              |
| --------------- | --------------- | -------------------------------------------------------- |
| CART            | Cart            | Provisional selection of products before checkout.       |
| PENDING_PAYMENT | Pending Payment | Waiting for financial confirmation from the gateway.     |
| PAID            | Paid            | Payment confirmed; preparation processes initiated.      |
| DISPATCHED      | Dispatched      | Physical departure from the warehouse for transportation.|
| DELIVERED       | Delivered       | Satisfactory conclusion and handover of the delivery.    |

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

# Value Object Design Rules

## Immutability

All Value Objects must be immutable after creation.

Their values cannot be modified after the object has been instantiated.

## Equality

Value Objects are compared according to their values rather than object identity.

Two instances containing the same business values represent the same Value Object.

## Controlled Values

Business catalogs must use controlled values defined by the domain.

The application must avoid replacing these concepts with arbitrary strings such as:

```text
"ACTIVE"
"PUBLISHED"
"PAID"
```

throughout the codebase.

Instead, the corresponding Value Object must be used:

```text
SystemRole
UserStatus
ProductType
ProductStatus
WarehouseType
OrderStatus
```

## Relationship With Entities

Entities reference Value Objects rather than primitive strings whenever the referenced value represents a controlled business concept.

Examples:

```text
User.role : SystemRole

User.status : UserStatus

Buyer.commercialStatus : CommercialStatus

Product.productType : ProductType

Product.status : ProductStatus

Warehouse.warehouseType : WarehouseType

Order.status : OrderStatus
```

This approach improves type safety, domain expressiveness, maintainability, and consistency with Domain-Driven Design principles.