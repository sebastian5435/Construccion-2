package application.adapters.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SellerRegistrationRequest(
        @NotBlank String identifier,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotNull @Valid WarehouseRequest firstWarehouse) {
}
