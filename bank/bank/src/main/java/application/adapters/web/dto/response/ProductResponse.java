package application.adapters.web.dto.response;

import application.domain.models.Product;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.util.List;

public record ProductResponse(
        String id,
        String name,
        String description,
        double price,
        ProductType productType,
        ProductStatus status,
        String sellerId,
        List<String> variants) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getProductType(), product.getStatus(),
                product.getSeller() == null ? null : product.getSeller().getIdentifier(),
                product.getVariants());
    }
}
