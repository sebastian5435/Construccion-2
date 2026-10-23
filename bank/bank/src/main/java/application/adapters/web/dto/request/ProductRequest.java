package application.adapters.web.dto.request;

import application.domain.valueobjects.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record ProductRequest(
        @NotBlank String name,
        String description,
        @NotNull @PositiveOrZero Double price,
        @NotNull ProductType productType,
        List<String> variants) {
}
