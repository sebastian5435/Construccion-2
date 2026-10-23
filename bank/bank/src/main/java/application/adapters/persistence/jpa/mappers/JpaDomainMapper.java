package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InventoryEntity;
import application.adapters.persistence.jpa.entities.InventoryMovementEntity;
import application.adapters.persistence.jpa.entities.InvoiceEntity;
import application.adapters.persistence.jpa.entities.OrderEntity;
import application.adapters.persistence.jpa.entities.OrderLineEmbeddable;
import application.adapters.persistence.jpa.entities.ProductEntity;
import application.adapters.persistence.jpa.entities.RefundEntity;
import application.adapters.persistence.jpa.entities.ReturnEntity;
import application.adapters.persistence.jpa.entities.ShipmentEntity;
import application.adapters.persistence.jpa.entities.ShoppingCartEntity;
import application.adapters.persistence.jpa.entities.UserEntity;
import application.adapters.persistence.jpa.entities.WarehouseEntity;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.DigitalProduct;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Invoice;
import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.ShoppingCart;
import application.domain.models.Supervisor;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.ProductType;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Translates between domain models and JPA entities.
 * Related aggregates are referenced by id (EntityManager#getReference) so saving one aggregate
 * never overwrites another one.
 */
@Component
public class JpaDomainMapper {

    private final EntityManager entityManager;

    public JpaDomainMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // ---------- Users ----------

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User user = switch (entity.getRole()) {
            case BUYER -> toBuyer(entity);
            case SELLER -> new Seller();
            case LOGISTICS_OPERATOR -> new LogisticsOperator();
            case ADMINISTRATOR -> new Administrator();
            case SUPERVISOR -> new Supervisor();
        };
        user.setIdentifier(entity.getIdentifier());
        user.setFullName(entity.getFullName());
        user.setEmail(entity.getEmail());
        user.setRole(entity.getRole());
        user.setStatus(entity.getStatus());
        return user;
    }

    public UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setIdentifier(user.getIdentifier());
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        entity.setRole(user.getRole());
        entity.setStatus(user.getStatus());
        if (user instanceof Buyer buyer) {
            entity.setMainAddress(buyer.getMainAddress());
            entity.setAdditionalAddresses(buyer.getAdditionalAddresses() == null
                    ? new ArrayList<>()
                    : new ArrayList<>(buyer.getAdditionalAddresses()));
            entity.setCommercialStatus(buyer.getCommercialStatus());
        }
        return entity;
    }

    private Buyer toBuyer(UserEntity entity) {
        Buyer buyer = new Buyer();
        buyer.setMainAddress(entity.getMainAddress());
        buyer.setAdditionalAddresses(new ArrayList<>(entity.getAdditionalAddresses()));
        buyer.setCommercialStatus(entity.getCommercialStatus());
        return buyer;
    }

    // ---------- Warehouses ----------

    public Warehouse toDomain(WarehouseEntity entity) {
        if (entity == null) {
            return null;
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setId(entity.getId());
        warehouse.setName(entity.getName());
        warehouse.setAddress(entity.getAddress());
        warehouse.setWarehouseType(entity.getWarehouseType());
        warehouse.setSeller((Seller) toDomain(entity.getSeller()));
        return warehouse;
    }

    public WarehouseEntity toEntity(Warehouse warehouse) {
        WarehouseEntity entity = new WarehouseEntity();
        entity.setId(warehouse.getId());
        entity.setName(warehouse.getName());
        entity.setAddress(warehouse.getAddress());
        entity.setWarehouseType(warehouse.getWarehouseType());
        entity.setSeller(userReference(warehouse.getSeller()));
        return entity;
    }

    // ---------- Products ----------

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        Product product = entity.getProductType() == ProductType.PHYSICAL
                ? new PhysicalProduct()
                : new DigitalProduct();
        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setPrice(entity.getPrice());
        product.setStatus(entity.getStatus());
        product.setVariants(new ArrayList<>(entity.getVariants()));
        product.setSeller((Seller) toDomain(entity.getSeller()));
        return product;
    }

    public ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setProductType(product.getProductType());
        entity.setStatus(product.getStatus());
        entity.setVariants(product.getVariants() == null
                ? new ArrayList<>()
                : new ArrayList<>(product.getVariants()));
        entity.setSeller(userReference(product.getSeller()));
        return entity;
    }

    // ---------- Inventory ----------

    public Inventory toDomain(InventoryEntity entity) {
        if (entity == null) {
            return null;
        }
        Inventory inventory = new Inventory();
        inventory.setId(entity.getId());
        inventory.setProduct(toDomain(entity.getProduct()));
        inventory.setWarehouse(toDomain(entity.getWarehouse()));
        inventory.setQuantity(entity.getQuantity());
        inventory.setReservedQuantity(entity.getReservedQuantity());
        inventory.setStatus(entity.getStatus());
        return inventory;
    }

    public InventoryEntity toEntity(Inventory inventory) {
        InventoryEntity entity = new InventoryEntity();
        entity.setId(inventory.getId());
        entity.setProduct(reference(ProductEntity.class, inventory.getProduct().getId()));
        entity.setWarehouse(reference(WarehouseEntity.class, inventory.getWarehouse().getId()));
        entity.setQuantity(inventory.getQuantity());
        entity.setReservedQuantity(inventory.getReservedQuantity());
        entity.setStatus(inventory.getStatus());
        return entity;
    }

    public InventoryMovement toDomain(InventoryMovementEntity entity) {
        InventoryMovement movement = new InventoryMovement();
        movement.setId(entity.getId());
        movement.setInventory(toDomain(entity.getInventory()));
        movement.setType(entity.getType());
        movement.setQuantity(entity.getQuantity());
        movement.setPerformedBy(toDomain(entity.getPerformedBy()));
        movement.setReference(entity.getReference());
        movement.setOccurredAt(entity.getOccurredAt());
        return movement;
    }

    public InventoryMovementEntity toEntity(InventoryMovement movement) {
        InventoryMovementEntity entity = new InventoryMovementEntity();
        entity.setId(movement.getId());
        entity.setInventory(reference(InventoryEntity.class, movement.getInventory().getId()));
        entity.setType(movement.getType());
        entity.setQuantity(movement.getQuantity());
        entity.setPerformedBy(userReference(movement.getPerformedBy()));
        entity.setReference(movement.getReference());
        entity.setOccurredAt(movement.getOccurredAt());
        return entity;
    }

    // ---------- Cart and orders ----------

    public ShoppingCart toDomain(ShoppingCartEntity entity) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(entity.getId());
        cart.setBuyer((Buyer) toDomain(entity.getBuyer()));
        cart.setItems(toDomainItems(entity.getItems()));
        return cart;
    }

    public ShoppingCartEntity toEntity(ShoppingCart cart) {
        ShoppingCartEntity entity = new ShoppingCartEntity();
        entity.setId(cart.getId());
        entity.setBuyer(userReference(cart.getBuyer()));
        entity.setItems(toEntityItems(cart.getItems()));
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        Order order = new Order();
        order.setId(entity.getId());
        order.setBuyer((Buyer) toDomain(entity.getBuyer()));
        order.setItems(toDomainItems(entity.getItems()));
        order.setStatus(entity.getStatus());
        order.setDeliveryAddress(entity.getDeliveryAddress());
        order.setTotalAmount(entity.getTotalAmount());
        order.setCreatedAt(entity.getCreatedAt());
        order.setUpdatedAt(entity.getUpdatedAt());
        return order;
    }

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setBuyer(userReference(order.getBuyer()));
        entity.setItems(toEntityItems(order.getItems()));
        entity.setStatus(order.getStatus());
        entity.setDeliveryAddress(order.getDeliveryAddress());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        return entity;
    }

    private List<OrderItem> toDomainItems(List<OrderLineEmbeddable> lines) {
        List<OrderItem> items = new ArrayList<>();
        for (OrderLineEmbeddable line : lines) {
            OrderItem item = new OrderItem();
            item.setProduct(toDomain(line.getProduct()));
            item.setQuantity(line.getQuantity());
            item.setUnitPrice(line.getUnitPrice());
            item.setSourceWarehouse(toDomain(line.getSourceWarehouse()));
            items.add(item);
        }
        return items;
    }

    private List<OrderLineEmbeddable> toEntityItems(List<OrderItem> items) {
        List<OrderLineEmbeddable> lines = new ArrayList<>();
        for (OrderItem item : items) {
            OrderLineEmbeddable line = new OrderLineEmbeddable();
            line.setProduct(reference(ProductEntity.class, item.getProduct().getId()));
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getUnitPrice());
            if (item.getSourceWarehouse() != null) {
                line.setSourceWarehouse(reference(WarehouseEntity.class, item.getSourceWarehouse().getId()));
            }
            lines.add(line);
        }
        return lines;
    }

    // ---------- Invoices, shipments, returns and refunds ----------

    public Invoice toDomain(InvoiceEntity entity) {
        Invoice invoice = new Invoice();
        invoice.setId(entity.getId());
        invoice.setOrder(toDomain(entity.getOrder()));
        invoice.setTotalAmount(entity.getTotalAmount());
        invoice.setIssueDate(entity.getIssueDate());
        return invoice;
    }

    public InvoiceEntity toEntity(Invoice invoice) {
        InvoiceEntity entity = new InvoiceEntity();
        entity.setId(invoice.getId());
        entity.setOrder(reference(OrderEntity.class, invoice.getOrder().getId()));
        entity.setTotalAmount(invoice.getTotalAmount());
        entity.setIssueDate(invoice.getIssueDate());
        return entity;
    }

    public Shipment toDomain(ShipmentEntity entity) {
        Shipment shipment = new Shipment();
        shipment.setTrackingId(entity.getTrackingId());
        shipment.setOrder(toDomain(entity.getOrder()));
        shipment.setLogisticsOperator((LogisticsOperator) toDomain(entity.getLogisticsOperator()));
        shipment.setDestinationAddress(entity.getDestinationAddress());
        shipment.setCreatedAt(entity.getCreatedAt());
        shipment.setDispatchedAt(entity.getDispatchedAt());
        shipment.setDeliveredAt(entity.getDeliveredAt());
        return shipment;
    }

    public ShipmentEntity toEntity(Shipment shipment) {
        ShipmentEntity entity = new ShipmentEntity();
        entity.setTrackingId(shipment.getTrackingId());
        entity.setOrder(reference(OrderEntity.class, shipment.getOrder().getId()));
        entity.setLogisticsOperator(userReference(shipment.getLogisticsOperator()));
        entity.setDestinationAddress(shipment.getDestinationAddress());
        entity.setCreatedAt(shipment.getCreatedAt());
        entity.setDispatchedAt(shipment.getDispatchedAt());
        entity.setDeliveredAt(shipment.getDeliveredAt());
        return entity;
    }

    public Return toDomain(ReturnEntity entity) {
        Return returnProcess = new Return();
        returnProcess.setId(entity.getId());
        returnProcess.setOrder(toDomain(entity.getOrder()));
        returnProcess.setReason(entity.getReason());
        returnProcess.setStatus(entity.getStatus());
        returnProcess.setRequestedAt(entity.getRequestedAt());
        returnProcess.setResolvedAt(entity.getResolvedAt());
        returnProcess.setResolvedBy((Administrator) toDomain(entity.getResolvedBy()));
        return returnProcess;
    }

    public ReturnEntity toEntity(Return returnProcess) {
        ReturnEntity entity = new ReturnEntity();
        entity.setId(returnProcess.getId());
        entity.setOrder(reference(OrderEntity.class, returnProcess.getOrder().getId()));
        entity.setReason(returnProcess.getReason());
        entity.setStatus(returnProcess.getStatus());
        entity.setRequestedAt(returnProcess.getRequestedAt());
        entity.setResolvedAt(returnProcess.getResolvedAt());
        entity.setResolvedBy(userReference(returnProcess.getResolvedBy()));
        return entity;
    }

    public Refund toDomain(RefundEntity entity) {
        Refund refund = new Refund();
        refund.setId(entity.getId());
        refund.setReturnProcess(toDomain(entity.getReturnProcess()));
        refund.setAmount(entity.getAmount());
        refund.setIssuedAt(entity.getIssuedAt());
        return refund;
    }

    public RefundEntity toEntity(Refund refund) {
        RefundEntity entity = new RefundEntity();
        entity.setId(refund.getId());
        entity.setReturnProcess(reference(ReturnEntity.class, refund.getReturnProcess().getId()));
        entity.setAmount(refund.getAmount());
        entity.setIssuedAt(refund.getIssuedAt());
        return entity;
    }

    // ---------- References ----------

    private UserEntity userReference(User user) {
        return user == null ? null : reference(UserEntity.class, user.getIdentifier());
    }

    private <T> T reference(Class<T> type, String id) {
        return entityManager.getReference(type, id);
    }
}
