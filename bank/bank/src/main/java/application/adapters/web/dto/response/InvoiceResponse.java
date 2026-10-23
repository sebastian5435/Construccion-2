package application.adapters.web.dto.response;

import application.domain.models.Invoice;
import java.time.LocalDateTime;

public record InvoiceResponse(
        String id,
        String orderId,
        double totalAmount,
        LocalDateTime issueDate) {

    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(invoice.getId(), invoice.getOrder().getId(),
                invoice.getTotalAmount(), invoice.getIssueDate());
    }
}
