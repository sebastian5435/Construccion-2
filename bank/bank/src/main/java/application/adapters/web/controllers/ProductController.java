package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.ProductRequest;
import application.adapters.web.dto.response.ProductResponse;
import application.domain.models.DigitalProduct;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.ports.input.ProductCatalogUseCase;
import application.domain.valueobjects.ProductType;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductCatalogUseCase productCatalog;

    public ProductController(ProductCatalogUseCase productCatalog) {
        this.productCatalog = productCatalog;
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                         @Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productCatalog.createProduct(toProduct(request), requesterId));
    }

    @PutMapping("/products/{productId}")
    public ProductResponse updateProduct(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                         @PathVariable String productId,
                                         @Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(productCatalog.updateProduct(productId, toProduct(request), requesterId));
    }

    @PatchMapping("/products/{productId}/publish")
    public ProductResponse publishProduct(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                          @PathVariable String productId) {
        return ProductResponse.from(productCatalog.publishProduct(productId, requesterId));
    }

    @PatchMapping("/products/{productId}/suspend")
    public ProductResponse suspendProduct(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                          @PathVariable String productId) {
        return ProductResponse.from(productCatalog.suspendProduct(productId, requesterId));
    }

    @PatchMapping("/products/{productId}/discontinue")
    public ProductResponse discontinueProduct(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                              @PathVariable String productId) {
        return ProductResponse.from(productCatalog.discontinueProduct(productId, requesterId));
    }

    @GetMapping("/products/{productId}")
    public ProductResponse findProductById(@PathVariable String productId) {
        return ProductResponse.from(productCatalog.findProductById(productId));
    }

    @GetMapping("/products")
    public List<ProductResponse> listPublishedProducts() {
        return productCatalog.listPublishedProducts().stream().map(ProductResponse::from).toList();
    }

    @GetMapping("/sellers/{sellerId}/products")
    public List<ProductResponse> listProductsBySeller(@PathVariable String sellerId) {
        return productCatalog.listProductsBySeller(sellerId).stream().map(ProductResponse::from).toList();
    }

    private Product toProduct(ProductRequest request) {
        Product product = request.productType() == ProductType.PHYSICAL
                ? new PhysicalProduct()
                : new DigitalProduct();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setVariants(request.variants() == null
                ? new ArrayList<>()
                : new ArrayList<>(request.variants()));
        return product;
    }
}
