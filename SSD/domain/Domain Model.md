# Domain Model - NexusMarket

## Introduction

The Domain Model defines the core business entities for the NexusMarket platform. These entities encapsulate the information, business rules, relationships, and lifecycles described in the functional specification.

The model follows Object-Oriented Design (OOD) and Domain-Driven Design (DDD) principles. Inheritance is applied for genuine domain specializations, and direct object associations are prioritized over generic identifier fields.

The ecosystem is composed of:
*   **Users:** Authenticated participants in the system (Buyers, Sellers, Logistics Operators, Administrators, Supervisors).
*   **Catalog and Inventory:** Management of Products, Warehouses, and physical Inventory.
*   **Commercial Operation:** The flow that originates in the Shopping Cart, formalizes as an Order, and concludes with Logistics (Shipment) and Billing (Invoice).

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
```

---

## Domain Relationships

```text
User
   │
   ├── Buyer
   │      ├── manages ───────────> ShoppingCart
   │      └── creates ───────────> Order
   │
   └── Seller
          └── manages ───────────> Product

Product
   │
   └── requires ─────────────────> Inventory

Warehouse
   │
   └── stores ───────────────────> Inventory

ShoppingCart
   │
   └── contains ─────────────────> Product

Order
   ├── created by ───────────────> Buyer
   ├── contains ─────────────────> Product
   ├── generates ────────────────> Invoice
   └── triggers ─────────────────> Shipment
```

---

## Core Entities

---

### User (Abstract)

#### Description
Represents any participant authorized to interact with the Marketplace. It centralizes identity, contact, and operational security information. Every user is assigned a strict single role.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| identifier | String | Unique identifier or document within the system. |
| fullName | String | Official and legal name of the user. |
| email | String | Primary channel for communication and access. |
| role | String | Assigned business role. |
| status | String | Current operational condition (e.g., Active, Blocked). |

---

### Buyer

#### Description
A specialized user who acquires products within the platform. Manages temporary selections and consolidates commercial transactions.

#### Inherits From
`User`

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| mainAddress | String | Default location for logistics and deliveries. |
| additionalAddresses | List<String> | Alternative delivery points. |
| commercialStatus | String | Current capacity to execute purchases. |

---

### Seller

#### Description
A specialized user responsible for populating the Marketplace catalog. Manages the information of commercialized goods and cannot self-register in the system (must be incorporated by an Administrator).

#### Inherits From
`User`

*(Note: Inherits all identity and status attributes from the parent class; no additional specific attributes are required in this phase of the domain).*

---

### Product

#### Description
Represents the goods (physical or digital) that make up the public NexusMarket catalog.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique catalog identifier. |
| productType | String | Classification of the good as Physical or Digital. |
| variants | List<String> | Differentiators (e.g., size, color, model). |
| status | String | Visibility lifecycle (Published, Suspended, Discontinued). |

---

### Warehouse

#### Description
Physical or logical storage space where physical product stock is controlled.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Infrastructure identifier. |
| name | String | Commercial designation of the warehouse. |
| warehouseType | String | Operational classification (Marketplace or Seller). |

---

### Inventory

#### Description
Transactional entity that links products to their physical location and controls real-time availability.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| product | Product | Commercialized good being quantified. |
| warehouse | Warehouse | Location where the merchandise resides. |
| quantity | int | Available amount. **Constraint:** Can never be less than 0. |

---

### ShoppingCart

#### Description
Temporary and preparatory grouping of products selected by a buyer before payment formalization.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| buyer | Buyer | User who owns the shopping session. |
| selectedProducts | List<Product> | Temporarily added goods. |

---

### Order

#### Description
The transactional heart of the system. Formalizes the purchase intent and triggers financial and logistical flows.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Official transaction identifier. |
| buyer | Buyer | Owner of the order. |
| products | List<Product> | Goods confirmed for purchase. |
| status | String | Current execution phase. |

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
DELIVERED / FINALIZED
```

---

### Invoice

#### Description
Commercial document that financially supports a successful order.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| id | String | Fiscal consecutive number. |
| order | Order | Base commercial transaction. |
| totalAmount | double | Total settled amount. |

---

### Shipment

#### Description
Entity that encapsulates the logistical tracking of physical products from the warehouse to the buyer's hands.

#### Attributes

| Attribute | Type | Description |
| :--- | :--- | :--- |
| trackingId | String | Tracking or guide number. |
| order | Order | Order requiring physical dispatch. |
| logisticsOperator | String | Company or entity responsible for transportation. |

---

## Domain Design Rules

1. **Inventory Constraints:** The system protects stock integrity through direct domain validations (encapsulation). Under no circumstances can an `Inventory` object be instantiated or modified with negative quantities.
2. **Transactional Immutability:** The lifecycle of an `Order` is unidirectional. Once an order reaches its final state, its attributes are immutable.
3. **Object-Relational Mapping:** Direct object references are prioritized (e.g., `order : Order` within `Invoice`) instead of storing foreign keys (`orderId`), respecting pure DDD principles.