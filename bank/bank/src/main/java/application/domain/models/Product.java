package application.domain.models;

import application.domain.exceptions.InvalidOperationException;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import java.util.List;

public abstract class Product {
    private String id;
    private String name;
    private String description;
    private double price;
    private Seller seller;
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

    public List<String> getVariants() { return variants; }
    public void setVariants(List<String> variants) { this.variants = variants; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public abstract ProductType getProductType();

    public boolean isPhysical() {
        return getProductType() == ProductType.PHYSICAL;
    }

    public boolean isPublished() {
        return status == ProductStatus.PUBLISHED;
    }

    public boolean isOwnedBy(String sellerId) {
        return seller != null && seller.getIdentifier().equals(sellerId);
    }

    public void publish() {
        requireNotDiscontinued("republished");
        this.status = ProductStatus.PUBLISHED;
    }

    public void suspend() {
        requireNotDiscontinued("suspended");
        this.status = ProductStatus.SUSPENDED;
    }

    public void discontinue() {
        this.status = ProductStatus.DISCONTINUED;
    }

    public void requireNotDiscontinued(String action) {
        if (status == ProductStatus.DISCONTINUED) {
            throw new InvalidOperationException("Discontinued products cannot be " + action + ".");
        }
    }
}
