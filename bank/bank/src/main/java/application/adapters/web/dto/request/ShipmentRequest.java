package application.adapters.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShipmentRequest(
        @NotBlank String orderId) {
}
