package application.domain.ports.input;

import application.domain.models.OperationalReport;
import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;
import java.util.List;

/**
 * Consolidates administrative information for consultation by Supervisors and Administrators.
 * Covers business objective OBJ-12.
 */
public interface MarketplaceReportUseCase {

    OperationalReport generateOperationalReport(String requesterId);

    List<Order> listOrdersByStatus(OrderStatus status, String requesterId);
}
