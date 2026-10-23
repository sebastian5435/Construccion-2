package application.adapters.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequest(
        @NotBlank String identifier,
        @NotBlank String fullName,
        @NotBlank @Email String email) {
}
