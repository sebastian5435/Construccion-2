package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.response.OperationalReportResponse;
import application.adapters.web.dto.response.OrderResponse;
import application.domain.ports.input.MarketplaceReportUseCase;
import application.domain.valueobjects.OrderStatus;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final MarketplaceReportUseCase marketplaceReport;

    public ReportController(MarketplaceReportUseCase marketplaceReport) {
        this.marketplaceReport = marketplaceReport;
    }

    @GetMapping("/operational")
    public OperationalReportResponse generateOperationalReport(
            @RequestHeader(RequestHeaders.USER_ID) String requesterId) {
        return OperationalReportResponse.from(marketplaceReport.generateOperationalReport(requesterId));
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrdersByStatus(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                                  @RequestParam OrderStatus status) {
        return marketplaceReport.listOrdersByStatus(status, requesterId).stream().map(OrderResponse::from).toList();
    }
}
