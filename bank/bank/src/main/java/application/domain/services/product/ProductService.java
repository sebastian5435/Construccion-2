package application.domain.services.product;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.UnauthorizedRoleException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.ports.input.ProductCatalogUseCase;
import application.domain.ports.output.InventoryRepositoryPort;
import application.domain.ports.output.ProductRepositoryPort;
import application.domain.services.access.AccessGuard;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.SystemRole;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService implements ProductCatalogUseCase {

    private final ProductRepositoryPort productRepository;
    private final InventoryRepositoryPort inventoryRepository;
    private final AccessGuard accessGuard;

    public ProductService(ProductRepositoryPort productRepository,
                          InventoryRepositoryPort inventoryRepository,
                          AccessGuard accessGuard) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.accessGuard = accessGuard;
    }

    @Override
    public Product createProduct(Product product, String sellerId) {
        Seller seller = requireSeller(sellerId, "createProduct");
        validateProductAttributes(product);
        product.setSeller(seller);
        product.setStatus(ProductStatus.SUSPENDED);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String productId, Product updatedProduct, String sellerId) {
        requireSeller(sellerId, "updateProduct");
        Product existing = findProductById(productId);
        requireOwnership(existing, sellerId);
        existing.requireNotDiscontinued("updated");
        validateProductAttributes(updatedProduct);
        if (updatedProduct.getProductType() != existing.getProductType()) {
            throw new InvalidOperationException("The product type cannot be changed once registered.");
        }
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setVariants(updatedProduct.getVariants());
        return productRepository.save(existing);
    }

    @Override
    public Product publishProduct(String productId, String sellerId) {
        requireSeller(sellerId, "publishProduct");
        Product product = findProductById(productId);
        requireOwnership(product, sellerId);
        if (product.isPhysical() && !hasAvailableStock(productId)) {
            throw new InvalidOperationException(
                    "Physical products need available inventory in at least one warehouse before being published.");
        }
        product.publish();
        return productRepository.save(product);
    }

    @Override
    public Product suspendProduct(String productId, String sellerId) {
        requireSeller(sellerId, "suspendProduct");
        Product product = findProductById(productId);
        requireOwnership(product, sellerId);
        product.suspend();
        return productRepository.save(product);
    }

    @Override
    public Product discontinueProduct(String productId, String sellerId) {
        requireSeller(sellerId, "discontinueProduct");
        Product product = findProductById(productId);
        requireOwnership(product, sellerId);
        product.discontinue();
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    @Override
    public List<Product> listPublishedProducts() {
        return productRepository.findByStatus(ProductStatus.PUBLISHED);
    }

    @Override
    public List<Product> listProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    private boolean hasAvailableStock(String productId) {
        return inventoryRepository.findByProduct(productId).stream()
                .anyMatch(inventory -> !inventory.isDamaged() && inventory.getAvailableQuantity() > 0);
    }

    private void validateProductAttributes(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new InvalidOperationException("Product name is required.");
        }
        if (product.getPrice() < 0) {
            throw new InvalidOperationException("Product price cannot be negative.");
        }
    }

    private void requireOwnership(Product product, String sellerId) {
        if (!product.isOwnedBy(sellerId)) {
            throw new UnauthorizedRoleException(
                    "modify product " + product.getId(),
                    "the seller who created it");
        }
    }

    private Seller requireSeller(String userId, String action) {
        return (Seller) accessGuard.requireActiveUser(userId, action, SystemRole.SELLER);
    }
}
