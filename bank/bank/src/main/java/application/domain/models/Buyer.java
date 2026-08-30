package application.domain.models;
import application.domain.valueobjects.CommercialStatus;
import java.util.List;

public class Buyer extends User {
    private String mainAddress;
    private List<String> additionalAddresses;
    private CommercialStatus commercialStatus;

    public Buyer() {}
    public String getMainAddress() { return mainAddress; }
    public void setMainAddress(String mainAddress) { this.mainAddress = mainAddress; }
    public List<String> getAdditionalAddresses() { return additionalAddresses; }
    public void setAdditionalAddresses(List<String> additionalAddresses) { this.additionalAddresses = additionalAddresses; }
    public CommercialStatus getCommercialStatus() { return commercialStatus; }
    public void setCommercialStatus(CommercialStatus commercialStatus) { this.commercialStatus = commercialStatus; }
}