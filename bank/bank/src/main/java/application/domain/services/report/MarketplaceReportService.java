package application.domain.services.report;

import application.domain.models.Inventory;
import application.domain.models.Invoice;
import application.domain.models.OperationalReport;
import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.ports.input.MarketplaceReportUseCase;
import application.domain.ports.output.InventoryRepositoryPort;
import application.domain.ports.output.InvoiceRepositoryPort;
import application.domain.ports.output.OrderRepositoryPort;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.ports.output.RefundRepositoryPort;
import application.domain.ports.output.ReturnRepositoryPort;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class MarketplaceReportService implements MarketplaceReportUseCase {

    private final UserRepositoryPort userRepository;
    private final ProductRepositoryPort productRepository;
    private final OrderRepositoryPort orderRepository;
    private final InvoiceRepositoryPort invoiceRepository;
    private final ReturnRepositoryPort returnRepository;
    private final RefundRepositoryPort refundRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final AccessGuard accessGuard;

    public MarketplaceReportService(UserRepositoryPort userRepository,
                                    ProductRepositoryPort productRepository,
                                    OrderRepositoryPort orderRepository,
                                    InvoiceRepositoryPort invoiceRepository,
                                    ReturnRepositoryPort returnRepository,
                                    RefundRepositoryPort refundRepository,
                                    InventoryRepositoryPort inventoryRepository,
                                    AccessGuard accessGuard) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.returnRepository = returnRepository;
        this.refundRepository = refundRepository;
        this.inventoryRepository = inventoryRepository;
        this.accessGuard = accessGuard;
    }

    @Override
    public OperationalReport generateOperationalReport(String requesterId) {
        requireConsultant(requesterId, "generateOperationalReport");
        OperationalReport report = new OperationalReport();
        report.setGeneratedAt(LocalDateTime.now());
        report.setUsersByRole(countBy(userRepository.findAll(), User::getRole));
        report.setProductsByStatus(countBy(productRepository.findAll(), Product::getStatus));
        report.setOrdersByStatus(countBy(orderRepository.findAll(), Order::getStatus));
        report.setTotalInvoiced(invoiceRepository.findAll().stream()
                .mapToDouble(Invoice::getTotalAmount).sum());
        report.setTotalRefunded(refundRepository.findAll().stream()
                .mapToDouble(Refund::getAmount).sum());
        report.setPendingReturns(returnRepository.findAll().stream()
                .filter(returnProcess -> returnProcess.getStatus() == ReturnStatus.REQUESTED)
                .count());
        report.setDamagedInventoryRecords(inventoryRepository.findAll().stream()
                .filter(Inventory::isDamaged)
                .count());
        return report;
    }

    @Override
    public List<Order> listOrdersByStatus(OrderStatus status, String requesterId) {
        requireConsultant(requesterId, "listOrdersByStatus");
        return orderRepository.findByStatus(status);
    }

    private void requireConsultant(String requesterId, String action) {
        accessGuard.requireActiveUser(requesterId, action, SystemRole.SUPERVISOR, SystemRole.ADMINISTRATOR);
    }

    private <T, K> Map<K, Long> countBy(List<T> elements, Function<T, K> classifier) {
        return elements.stream().collect(Collectors.groupingBy(classifier, Collectors.counting()));
    }
}
