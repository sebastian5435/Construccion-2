package application.adapters.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRegistrationRequest(
        @NotBlank String productId,
        @NotBlank String warehouseId,
        @NotNull @PositiveOrZero Integer initialQuantity) {
}
