package application.domain.models;

import application.domain.valueobjects.ProductType;

public class DigitalProduct extends Product {

    @Override
    public ProductType getProductType() {
        return ProductType.DIGITAL;
    }
}
