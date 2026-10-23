package application.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import application.adapters.persistence.inmemory.InMemoryInventoryMovementRepository;
import application.adapters.persistence.inmemory.InMemoryInventoryRepository;
import application.adapters.persistence.inmemory.InMemoryInvoiceRepository;
import application.adapters.persistence.inmemory.InMemoryOrderRepository;
import application.adapters.persistence.inmemory.InMemoryProductRepository;
import application.adapters.persistence.inmemory.InMemoryRefundRepository;
import application.adapters.persistence.inmemory.InMemoryReturnRepository;
import application.adapters.persistence.inmemory.InMemoryShipmentRepository;
import application.adapters.persistence.inmemory.InMemoryShoppingCartRepository;
import application.adapters.persistence.inmemory.InMemoryUserRepository;
import application.adapters.persistence.inmemory.InMemoryWarehouseRepository;
import application.domain.exceptions.DomainException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.OrderAlreadyFinalizedException;
import application.domain.exceptions.InvalidOrderStateTransitionException;
import application.domain.exceptions.ProductNotAvailableException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.DigitalProduct;
import application.domain.models.Inventory;
import application.domain.models.LogisticsOperator;
import application.domain.models.OperationalReport;
import application.domain.models.Order;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.Supervisor;
import application.domain.models.Warehouse;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.services.cart.ShoppingCartService;
import application.domain.services.inventory.InventoryService;
import application.domain.services.inventory.StockMovementService;
import application.domain.services.order.OrderProcessingService;
import application.domain.services.product.ProductService;
import application.domain.services.report.MarketplaceReportService;
import application.domain.services.returnprocess.ReturnRefundService;
import application.domain.services.shipment.ShipmentService;
import application.domain.services.user.UserService;
import application.domain.services.warehouse.WarehouseService;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketplaceFlowTest {

    private UserService userService;
    private WarehouseService warehouseService;
    private ProductService productService;
    private InventoryService inventoryService;
    private ShoppingCartService cartService;
    private OrderProcessingService orderService;
    private ShipmentService shipmentService;
    private ReturnRefundService returnRefundService;
    private MarketplaceReportService reportService;

    private String sellerWarehouseId;

    @BeforeEach
    void setUp() {
        UserRepositoryPort users = new InMemoryUserRepository();
        InMemoryWarehouseRepository warehouses = new InMemoryWarehouseRepository();
        InMemoryProductRepository products = new InMemoryProductRepository();
        InMemoryInventoryRepository inventories = new InMemoryInventoryRepository();
        InMemoryInventoryMovementRepository movements = new InMemoryInventoryMovementRepository();
        InMemoryShoppingCartRepository carts = new InMemoryShoppingCartRepository();
        InMemoryOrderRepository orders = new InMemoryOrderRepository();
        InMemoryInvoiceRepository invoices = new InMemoryInvoiceRepository();
        InMemoryShipmentRepository shipments = new InMemoryShipmentRepository();
        InMemoryReturnRepository returns = new InMemoryReturnRepository();
        InMemoryRefundRepository refunds = new InMemoryRefundRepository();

        AccessGuard guard = new AccessGuard(users);
        StockMovementService stock = new StockMovementService(inventories, movements);
        userService = new UserService(users, warehouses, guard);
        warehouseService = new WarehouseService(warehouses, users, guard);
        productService = new ProductService(products, inventories, guard);
        inventoryService = new InventoryService(inventories, movements, products, warehouses, stock, guard);
        cartService = new ShoppingCartService(carts, products, guard);
        orderService = new OrderProcessingService(orders, invoices, carts, stock, guard);
        shipmentService = new ShipmentService(shipments, orders, stock, guard);
        returnRefundService = new ReturnRefundService(returns, refunds, orders, stock, guard);
        reportService = new MarketplaceReportService(users, products, orders, invoices, returns, refunds,
                inventories, guard);

        Administrator admin = user(new Administrator(), "admin", SystemRole.ADMINISTRATOR);
        users.save(admin);

        Warehouse firstWarehouse = new Warehouse();
        firstWarehouse.setName("Seller main warehouse");
        firstWarehouse.setAddress("Calle 10 # 20-30");
        userService.registerSeller(user(new Seller(), "seller", null), firstWarehouse, "admin");
        userService.registerSeller(user(new Seller(), "other-seller", null), warehouse("Other"), "admin");
        sellerWarehouseId = warehouseService.listWarehousesBySeller("seller").get(0).getId();

        userService.registerLogisticsOperator(user(new LogisticsOperator(), "operator", null), "admin");
        userService.registerSupervisor(user(new Supervisor(), "supervisor", null), "admin");

        Buyer buyer = user(new Buyer(), "buyer", null);
        buyer.setMainAddress("Carrera 50 # 10-20");
        userService.registerBuyer(buyer);
    }

    @Test
    void physicalOrderGoesThroughTheWholeLifecycle() {
        Product product = physicalProduct("Keyboard", 100.0);
        assertThrows(InvalidOperationException.class,
                () -> productService.publishProduct(product.getId(), "seller"));

        inventoryService.registerInventory(product.getId(), sellerWarehouseId, 10, "seller");
        productService.publishProduct(product.getId(), "seller");

        cartService.addProductToCart("buyer", product.getId(), 3);
        Order order = orderService.placeOrder("buyer", null);
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(300.0, order.getTotalAmount());
        assertEquals(7, inventoryService.checkAvailability(product.getId(), sellerWarehouseId));

        orderService.payOrder(order.getId(), "buyer");
        assertNotNull(orderService.getInvoiceByOrder(order.getId()));

        Shipment shipment = shipmentService.createShipment(order.getId(), "operator");
        shipmentService.dispatchShipment(shipment.getTrackingId(), "operator");
        Inventory inventory = inventoryService.listInventoryByProduct(product.getId()).get(0);
        assertEquals(7, inventory.getQuantity());
        assertEquals(0, inventory.getReservedQuantity());

        shipmentService.confirmDelivery(shipment.getTrackingId(), "operator");
        assertEquals(OrderStatus.DELIVERED, orderService.findOrderById(order.getId()).getStatus());
        assertThrows(OrderAlreadyFinalizedException.class,
                () -> orderService.cancelOrder(order.getId(), "buyer"));

        Return returnProcess = returnRefundService.requestReturn(order.getId(), "buyer", "Defective keys");
        Refund refund = returnRefundService.generateRefund(returnProcess.getId(), "admin");
        assertEquals(300.0, refund.getAmount());
        assertEquals(ReturnStatus.APPROVED, returnRefundService.findReturnById(returnProcess.getId()).getStatus());
        assertEquals(10, inventoryService.checkAvailability(product.getId(), sellerWarehouseId));

        assertEquals(
                java.util.List.of(InventoryMovementType.ENTRY, InventoryMovementType.RESERVATION,
                        InventoryMovementType.SALE_EXIT, InventoryMovementType.RETURN),
                inventoryService.listMovements(product.getId(), sellerWarehouseId).stream()
                        .map(movement -> movement.getType()).toList());
    }

    @Test
    void digitalOrderIsDeliveredImmediatelyAfterPayment() {
        Product ebook = new DigitalProduct();
        ebook.setName("E-book");
        ebook.setPrice(20.0);
        Product created = productService.createProduct(ebook, "seller");
        productService.publishProduct(created.getId(), "seller");

        cartService.addProductToCart("buyer", created.getId(), 1);
        Order order = orderService.placeOrder("buyer", null);
        Order paid = orderService.payOrder(order.getId(), "buyer");

        assertEquals(OrderStatus.DELIVERED, paid.getStatus());
        assertThrows(InvalidOperationException.class,
                () -> shipmentService.createShipment(order.getId(), "operator"));
        assertThrows(InvalidOperationException.class,
                () -> inventoryService.registerInventory(created.getId(), sellerWarehouseId, 5, "seller"));
    }

    @Test
    void cancellingAPendingOrderReleasesTheReservation() {
        Product product = publishedPhysicalProduct(5);
        cartService.addProductToCart("buyer", product.getId(), 5);
        Order order = orderService.placeOrder("buyer", null);
        assertEquals(0, inventoryService.checkAvailability(product.getId(), sellerWarehouseId));

        orderService.cancelOrder(order.getId(), "buyer");
        assertEquals(OrderStatus.CANCELLED, orderService.findOrderById(order.getId()).getStatus());
        assertEquals(5, inventoryService.checkAvailability(product.getId(), sellerWarehouseId));
        assertThrows(OrderAlreadyFinalizedException.class,
                () -> orderService.payOrder(order.getId(), "buyer"));
    }

    @Test
    void paidOrdersCannotBeCancelled() {
        Product product = publishedPhysicalProduct(5);
        cartService.addProductToCart("buyer", product.getId(), 1);
        Order order = orderService.placeOrder("buyer", null);
        orderService.payOrder(order.getId(), "buyer");

        assertThrows(InvalidOrderStateTransitionException.class,
                () -> orderService.cancelOrder(order.getId(), "buyer"));
    }

    @Test
    void damagedOrInsufficientInventoryCannotBeReserved() {
        Product product = publishedPhysicalProduct(2);
        cartService.addProductToCart("buyer", product.getId(), 3);
        assertThrows(ProductNotAvailableException.class, () -> orderService.placeOrder("buyer", null));

        cartService.updateProductQuantity("buyer", product.getId(), 1);
        inventoryService.markAsDamaged(product.getId(), sellerWarehouseId, "operator");
        assertThrows(ProductNotAvailableException.class, () -> orderService.placeOrder("buyer", null));
    }

    @Test
    void participantsCannotActOutsideTheirRole() {
        Product product = publishedPhysicalProduct(5);

        assertThrows(UnauthorizedRoleException.class,
                () -> inventoryService.addStock(product.getId(), sellerWarehouseId, 5, "other-seller"));
        assertThrows(UnauthorizedRoleException.class,
                () -> inventoryService.addStock(product.getId(), sellerWarehouseId, 5, "buyer"));
        assertThrows(InvalidOperationException.class,
                () -> inventoryService.registerInventory(product.getId(),
                        warehouseService.listWarehousesBySeller("other-seller").get(0).getId(), 1, "operator"));
        assertThrows(UnauthorizedRoleException.class,
                () -> warehouseService.registerSellerWarehouse(warehouse("Extra"), "seller", "seller"));
        assertThrows(UnauthorizedRoleException.class,
                () -> reportService.generateOperationalReport("buyer"));

        cartService.addProductToCart("buyer", product.getId(), 1);
        Order order = orderService.placeOrder("buyer", null);
        assertThrows(UnauthorizedRoleException.class,
                () -> orderService.payOrder(order.getId(), "seller"));
        assertThrows(InvalidOperationException.class,
                () -> orderService.placeOrder("buyer", "Unknown address"));
    }

    @Test
    void supervisorConsultsTheOperationalReport() {
        Product product = publishedPhysicalProduct(5);
        cartService.addProductToCart("buyer", product.getId(), 2);
        Order order = orderService.placeOrder("buyer", null);
        orderService.payOrder(order.getId(), "buyer");

        OperationalReport report = reportService.generateOperationalReport("supervisor");
        assertEquals(1L, report.getOrdersByStatus().get(OrderStatus.PAID));
        assertEquals(2L, report.getUsersByRole().get(SystemRole.SELLER));
        assertEquals(200.0, report.getTotalInvoiced());
        assertEquals(1, reportService.listOrdersByStatus(OrderStatus.PAID, "admin").size());
    }

    @Test
    void blockedUsersCannotOperate() {
        userService.blockUser("buyer", "admin");
        assertThrows(DomainException.class, () -> cartService.createCart("buyer"));
        assertThrows(InvalidOperationException.class, () -> userService.blockUser("admin", "admin"));
    }

    private Product publishedPhysicalProduct(int stock) {
        Product product = physicalProduct("Mouse", 100.0);
        inventoryService.registerInventory(product.getId(), sellerWarehouseId, stock, "seller");
        return productService.publishProduct(product.getId(), "seller");
    }

    private Product physicalProduct(String name, double price) {
        Product product = new PhysicalProduct();
        product.setName(name);
        product.setPrice(price);
        return productService.createProduct(product, "seller");
    }

    private static Warehouse warehouse(String name) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(name);
        warehouse.setAddress("Address of " + name);
        return warehouse;
    }

    private static <T extends application.domain.models.User> T user(T user, String id, SystemRole role) {
        user.setIdentifier(id);
        user.setFullName("User " + id);
        user.setEmail(id + "@nexusmarket.com");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
