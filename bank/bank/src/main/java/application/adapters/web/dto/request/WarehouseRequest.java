package application.adapters.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WarehouseRequest(
        @NotBlank String name,
        @NotBlank String address) {
}
