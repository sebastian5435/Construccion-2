package application.adapters.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record BuyerRegistrationRequest(
        @NotBlank String identifier,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String mainAddress,
        List<String> additionalAddresses) {
}
