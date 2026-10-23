package application.adapters.web.dto.response;

import application.domain.models.OperationalReport;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.Map;

public record OperationalReportResponse(
        LocalDateTime generatedAt,
        Map<SystemRole, Long> usersByRole,
        Map<ProductStatus, Long> productsByStatus,
        Map<OrderStatus, Long> ordersByStatus,
        double totalInvoiced,
        double totalRefunded,
        long pendingReturns,
        long damagedInventoryRecords) {

    public static OperationalReportResponse from(OperationalReport report) {
        return new OperationalReportResponse(report.getGeneratedAt(), report.getUsersByRole(),
                report.getProductsByStatus(), report.getOrdersByStatus(), report.getTotalInvoiced(),
                report.getTotalRefunded(), report.getPendingReturns(), report.getDamagedInventoryRecords());
    }
}
