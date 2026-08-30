package application.domain.models;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductStatus;
import java.util.List;

public abstract class Product {
    private String id;
    private ProductType productType; 
    private List<String> variants;
    private ProductStatus status; 

    public Product() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    public List<String> getVariants() { return variants; }
    public void setVariants(List<String> variants) { this.variants = variants; }
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}