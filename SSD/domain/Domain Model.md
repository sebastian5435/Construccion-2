# Domain Model

## Introduction

The Domain Model represents the core business entities of the NexusMarket platform. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the system specification.

The model follows Object-Oriented Design (OOD) and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, while explicit object relationships and strongly-typed Domain Value Objects are preferred over primitive types.

The model distinguishes between:
* **Users:** Authenticated participants authorized to interact with the Marketplace (Buyer, Seller, Logistics Operator, Administrator, Supervisor).
* **Products:** Physical or digital goods offered on the platform.
* **Inventory & Warehouses:** Distributed stock managed across specialized physical storage locations.
* **Commercial Lifecycle:** The flow from temporary selection (Shopping Cart) to confirmed commercial commitment (Order), followed by logistical dispatch (Shipment), post-sale operations (Return, Refund), and financial records (Invoice).

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

ShoppingCart

Order

Invoice

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
 │ └── creates ────────────> Order
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

Warehouse
 └── stores ────────────────────> Inventory

Inventory
 ├── associated with ───────────> Product
 └── stored in ─────────────────> Warehouse

ShoppingCart
 ├── belongs to ────────────────> Buyer
 └── contains ──────────────────> Product

Order
 ├── associated with ───────────> Buyer
 ├── contains ──────────────────> Product
 ├── generates ─────────────────> Invoice
 ├── generates ─────────────────> Shipment
 └── may generate ──────────────> Return
                                    │
                                    └── may generate ──> Refund

Invoice
 └── associated with ───────────> Order
```

---

## Core Entities

---

### User (Abstract)

#### Description
Represents any participant authorized to interact with the NexusMarket Marketplace. It centralizes shared identity and operational access information. Every user is assigned a single strict role. This class cannot be instantiated directly.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| identifier | String | Unique identifier or document of the user. |
| fullName | String | Official full name of the user. |
| email | String | Primary email address for access and communication. |
| role | SystemRole | Business role assigned to the user. |
| status | UserStatus | Current operational status of the user. |

---

### Buyer

#### Description
Represents an authorized user who acquires products published on the Marketplace. Manages delivery locations and places commercial orders.

#### Inherits From
`User`

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| mainAddress | String | Primary location used for product deliveries. |
| additionalAddresses | List<String> | Secondary delivery locations registered by the buyer. |
| commercialStatus | CommercialStatus | Current commercial condition for making purchases. |

---

### Seller

#### Description
Represents a specialized user responsible for registering and commercializing products in the catalog. Sellers cannot self-register; they are incorporated by an Administrator.

#### Inherits From
`User`

---

### LogisticsOperator

#### Description
Represents the participant responsible for physical logistics, warehouse management, and shipment dispatch operations.

#### Inherits From
`User`

---

### Administrator

#### Description
Represents the participant responsible for administrative management, including incorporating sellers and registering their initial warehouses.

#### Inherits From
`User`

---

### Supervisor

#### Description
Represents the participant responsible for consultation, operational monitoring, and auditing across the Marketplace.

#### Inherits From
`User`

---

### Product (Abstract)

#### Description
Represents goods offered in the Marketplace catalog. This class cannot be instantiated directly.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the product. |
| productType | ProductType | Classification defining whether the good is Physical or Digital. |
| variants | List<String> | Differences in color, size, model, or technical specifications. |
| status | ProductStatus | Current publication status of the product. |

---

### PhysicalProduct

#### Description
Represents a tangible good that requires physical inventory management, warehouse storage, and shipment dispatch.

#### Inherits From
`Product`

---

### DigitalProduct

#### Description
Represents an intangible product delivered immediately after payment confirmation without physical logistics.

#### Inherits From
`Product`

---

### Warehouse

#### Description
Represents a physical storage location used for stock control.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the warehouse. |
| name | String | Commercial name of the warehouse. |
| warehouseType | WarehouseType | Classification of the warehouse (Marketplace or Seller). |

---

### Inventory

#### Description
Transactional entity that links products to their physical location and controls real-time stock availability. The system must not allow negative inventory under any circumstance.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| product | Product | Commercialized good being quantified. |
| warehouse | Warehouse | Location where the merchandise resides. |
| quantity | int | Available units. **Constraint:** Cannot be less than 0. |

#### Business Rules
- Inventory cannot have negative quantities.
- Inventory must be associated with a specific product and warehouse.
- Inventory that does not exist or is marked as damaged cannot be reserved.

---

### ShoppingCart

#### Description
Represents the temporary and preparatory selection of products made by a Buyer before confirming a purchase. Precedes the formal Order.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| buyer | Buyer | User who owns the shopping session. |
| selectedProducts | List\<Product\> | Products temporarily added before checkout. |

---

### Order

#### Description
Represents a formal commercial commitment placed by a Buyer. It drives the central transactional lifecycle of the platform.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier of the order. |
| buyer | Buyer | Buyer who placed the order. |
| products | List<Product> | Products included in the purchase. |
| status | OrderStatus | Current state within the order lifecycle. |

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

### Invoice

#### Description
Commercial document that financially supports a successful order. Generated automatically when an Order transitions to the PAID state.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Fiscal consecutive identifier. |
| order | Order | Base commercial transaction. |
| totalAmount | double | Total settled amount of the purchase. |

---

### Shipment

#### Description
Encapsulates the logistics and physical transport process of an order to the buyer's destination.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| trackingId | String | Tracking or dispatch guide identifier. |
| order | Order | Associated commercial order. |
| logisticsOperator | LogisticsOperator | Logistics operator in charge of the shipment. |

---

### Return

#### Description
Represents the post-sale reverse logistics process where a customer returns a product associated with an order.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier for the return process. |
| order | Order | Commercial order associated with the return. |

---

### Refund

#### Description
Represents the financial reimbursement process resulting from an approved product return.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique identifier for the refund transaction. |
| returnProcess | Return | The associated return process that triggered the refund. |

---

## Domain Design Rules

1. **Explicit Domain Value Objects:** Controlled business states, types, and roles are implemented using dedicated Domain Value Objects (`enum`) instead of arbitrary primitive strings.
2. **Object Associations:** Entities reference related domain objects directly (e.g., `LogisticsOperator` inside `Shipment`, `Buyer` inside `Order`) rather than untyped identifier strings.
3. **Strict Specialization:** Abstract base classes (`User`, `Product`) enforce code reuse and polymorphism across specific business actors and product types.