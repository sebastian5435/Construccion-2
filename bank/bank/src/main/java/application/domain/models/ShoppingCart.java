package application.domain.models;
import java.util.List;

public class ShoppingCart {
    private Buyer buyer;
    private List<Product> selectedProducts;

    public ShoppingCart() {}

    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }

    public List<Product> getSelectedProducts() { return selectedProducts; }
    public void setSelectedProducts(List<Product> selectedProducts) { this.selectedProducts = selectedProducts; }
}
