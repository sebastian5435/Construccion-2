package application.domain.models;
import java.util.List;

public abstract class Product {
    private String id;
    private String productType; 
    private List<String> variants;
    private String status; 

    public Product() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public List<String> getVariants() { return variants; }
    public void setVariants(List<String> variants) { this.variants = variants; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
