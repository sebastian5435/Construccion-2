package application.adapters.web.dto.response;

import application.domain.models.Return;
import application.domain.valueobjects.ReturnStatus;
import java.time.LocalDateTime;

public record ReturnResponse(
        String id,
        String orderId,
        String reason,
        ReturnStatus status,
        LocalDateTime requestedAt,
        LocalDateTime resolvedAt,
        String resolvedBy) {

    public static ReturnResponse from(Return returnProcess) {
        return new ReturnResponse(returnProcess.getId(), returnProcess.getOrder().getId(),
                returnProcess.getReason(), returnProcess.getStatus(), returnProcess.getRequestedAt(),
                returnProcess.getResolvedAt(),
                returnProcess.getResolvedBy() == null ? null : returnProcess.getResolvedBy().getIdentifier());
    }
}
