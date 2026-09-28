package application.domain.ports.input;

import application.domain.models.Product;
import java.util.List;

/**
 * Manages the product catalog: creation, updates, and lifecycle transitions.
 * Covers business objective OBJ-05.
 */
public interface ProductCatalogUseCase {

    Product createProduct(Product product, String sellerId);

    Product updateProduct(String productId, Product updatedProduct, String sellerId);

    Product publishProduct(String productId, String sellerId);

    Product suspendProduct(String productId, String sellerId);

    Product discontinueProduct(String productId, String sellerId);

    Product findProductById(String productId);

    List<Product> listPublishedProducts();

    List<Product> listProductsBySeller(String sellerId);
}
