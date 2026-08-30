package application.domain.models;
import java.util.List;

public class Buyer extends User {
    private String mainAddress;
    private List<String> additionalAddresses;
    private String commercialStatus;

    public Buyer() {}

    public String getMainAddress() { return mainAddress; }
    public void setMainAddress(String mainAddress) { this.mainAddress = mainAddress; }

    public List<String> getAdditionalAddresses() { return additionalAddresses; }
    public void setAdditionalAddresses(List<String> additionalAddresses) { this.additionalAddresses = additionalAddresses; }

    public String getCommercialStatus() { return commercialStatus; }
    public void setCommercialStatus(String commercialStatus) { this.commercialStatus = commercialStatus; }
}
