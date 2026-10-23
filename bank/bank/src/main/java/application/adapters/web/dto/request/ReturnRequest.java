package application.adapters.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReturnRequest(
        @NotBlank String orderId,
        @NotBlank String reason) {
}
