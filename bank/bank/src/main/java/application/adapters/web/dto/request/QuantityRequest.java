package application.adapters.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record QuantityRequest(
        @NotNull @PositiveOrZero Integer quantity) {
}
