package application.adapters.web.dto.response;

import application.domain.models.Refund;
import java.time.LocalDateTime;

public record RefundResponse(
        String id,
        String returnId,
        double amount,
        LocalDateTime issuedAt) {

    public static RefundResponse from(Refund refund) {
        return new RefundResponse(refund.getId(), refund.getReturnProcess().getId(),
                refund.getAmount(), refund.getIssuedAt());
    }
}
