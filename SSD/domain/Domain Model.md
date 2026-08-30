# Domain Model - NexusMarket

## Introduction

The Domain Model represents the core business entities of the NexusMarket platform[cite: 2]. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the system specification[cite: 2].

The model follows Object-Oriented Design (OOD) and Domain-Driven Design (DDD) principles[cite: 2]. Inheritance is used to represent genuine domain specialization, while explicit object relationships and strongly-typed Domain Value Objects are preferred over primitive types[cite: 1, 3].

The model distinguishes between:
*   **Users:** Authenticated participants authorized to interact with the Marketplace (Buyer, Seller, Logistics Operator, Administrator, Supervisor)[cite: 1, 2].
*   **Products:** Physical or digital goods offered on the platform[cite: 1, 2].
*   **Inventory & Warehouses:** Distributed stock managed across specialized physical storage locations[cite: 1, 2].
*   **Commercial Lifecycle:** The flow from temporary selection (Shopping Cart) to confirmed commercial commitment (Order), followed by logistical dispatch (Shipment), post-sale operations (Return, Refund), and financial records (Invoice)[cite: 1].

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

Order

Shipment

Return

Refund
```

---

## Domain Relationships

```text
User
   │
   ├── Buyer
   │      └── creates ────────────> Order
   │
   └── Seller
          └── manages ────────────> Product

LogisticsOperator
   └── manages ───────────────────> Shipment

Administrator
   ├── registers ─────────────────> Seller
   └── registers ─────────────────> Warehouse

Supervisor
   └── consults ──────────────────> Marketplace Information

Order
   ├── associated with ───────────> Buyer
   ├── contains ──────────────────> Product
   ├── generates ─────────────────> Shipment
   └── may generate ──────────────> Return
                                       │
                                       └── may generate ──> Refund
```

---

## Core Entities

---

### User (Abstract)

#### Description
Represents any participant authorized to interact with the NexusMarket Marketplace[cite: 1]. It centralizes shared identity and operational access information[cite: 1, 2]. Every user is assigned a single strict role[cite: 1]. This class cannot be instantiated directly[cite: 1].

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| identifier | String | Unique identifier or document of the user[cite: 1, 2]. |
| fullName | String | Official full name of the user[cite: 1, 2]. |
| email | String | Primary email address for access and communication[cite: 1, 2]. |
| role | SystemRole | Business role assigned to the user[cite: 1]. |
| status | UserStatus | Current operational status of the user[cite: 1]. |

---

### Buyer

#### Description
Represents an authorized user who acquires products published on the Marketplace[cite: 1, 2]. Manages delivery locations and places commercial orders[cite: 1, 2].

#### Inherits From
`User`[cite: 1, 2]

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| mainAddress | String | Primary location used for product deliveries[cite: 1, 2]. |
| additionalAddresses | List<String> | Secondary delivery locations registered by the buyer[cite: 1, 2]. |
| commercialStatus | CommercialStatus | Current commercial condition for making purchases[cite: 1]. |

---

### Seller

#### Description
Represents a specialized user responsible for registering and commercializing products in the catalog[cite: 1, 2]. Sellers cannot self-register; they are incorporated by an Administrator[cite: 1, 2].

#### Inherits From
`User`[cite: 1, 2]

---

### LogisticsOperator

#### Description
Represents the participant responsible for physical logistics, warehouse management, and shipment dispatch operations[cite: 1].

#### Inherits From
`User`[cite: 1]

---

### Administrator

#### Description
Represents the participant responsible for administrative management, including incorporating sellers and registering their initial warehouses[cite: 1].

#### Inherits From
`User`[cite: 1]

---

### Supervisor

#### Description
Represents the participant responsible for consultation, operational monitoring, and auditing across the Marketplace[cite: 1].

#### Inherits From
`User`[cite: 1]

---

### Product (Abstract)

#### Description
Represents goods offered in the Marketplace catalog[cite: 1, 2]. This class cannot be instantiated directly[cite: 1].

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the product[cite: 1, 2]. |
| productType | ProductType | Classification defining whether the good is Physical or Digital[cite: 1]. |
| variants | List<String> | Differences in color, size, model, or technical specifications[cite: 1, 2]. |
| status | ProductStatus | Current publication status of the product[cite: 1]. |

---

### PhysicalProduct

#### Description
Represents a tangible good that requires physical inventory management, warehouse storage, and shipment dispatch[cite: 1].

#### Inherits From
`Product`[cite: 1]

---

### DigitalProduct

#### Description
Represents an intangible product delivered immediately after payment confirmation without physical logistics[cite: 1].

#### Inherits From
`Product`[cite: 1]

---

### Warehouse

#### Description
Represents a physical storage location used for stock control[cite: 1, 2].

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the warehouse[cite: 1, 2]. |
| name | String | Commercial name of the warehouse[cite: 1, 2]. |
| warehouseType | WarehouseType | Classification of the warehouse (Marketplace or Seller)[cite: 1]. |

---

### Order

#### Description
Represents a formal commercial commitment placed by a Buyer[cite: 1, 2]. It drives the central transactional lifecycle of the platform[cite: 1, 2].

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the order[cite: 1, 2]. |
| buyer | Buyer | Buyer who placed the order[cite: 1]. |
| products | List<Product> | Products included in the purchase[cite: 1]. |
| status | OrderStatus | Current state within the order lifecycle[cite: 1]. |

#### Lifecycle
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

### Shipment

#### Description
Encapsulates the logistics and physical transport process of an order to the buyer's destination[cite: 1].

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| trackingId | String | Tracking or dispatch guide identifier. |
| order | Order | Associated commercial order[cite: 1]. |
| logisticsOperator | LogisticsOperator | Logistics operator in charge of the shipment. |

---

### Return

#### Description
Represents the post-sale reverse logistics process where a customer returns a product associated with an order[cite: 1].

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier for the return process. |
| order | Order | Commercial order associated with the return[cite: 1]. |

---

### Refund

#### Description
Represents the financial reimbursement process resulting from an approved product return[cite: 1].

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier for the refund transaction. |
| returnProcess | Return | The associated return process that triggered the refund[cite: 1]. |

---

## Domain Design Rules

1. **Explicit Domain Value Objects:** Controlled business states, types, and roles are implemented using dedicated Domain Value Objects (`enum`) instead of arbitrary primitive strings[cite: 1, 3].
2. **Object Associations:** Entities reference related domain objects directly (e.g., `LogisticsOperator` inside `Shipment`, `Buyer` inside `Order`) rather than untyped identifier strings[cite: 1].
3. **Strict Specialization:** Abstract base classes (`User`, `Product`) enforce code reuse and polymorphism across specific business actors and product types[cite: 1].