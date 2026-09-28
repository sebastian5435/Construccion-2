package application.domain.models;

import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.ProductStatus;
import java.util.List;

public abstract class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private Seller seller;
    private ProductType productType;
    private List<String> variants;
    private ProductStatus status;

    public Product() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }

    public List<String> getVariants() { return variants; }
    public void setVariants(List<String> variants) { this.variants = variants; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
}
