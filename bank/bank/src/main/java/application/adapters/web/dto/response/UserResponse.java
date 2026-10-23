package application.adapters.web.dto.response;

import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import java.util.List;

public record UserResponse(
        String identifier,
        String fullName,
        String email,
        SystemRole role,
        UserStatus status,
        String mainAddress,
        List<String> additionalAddresses,
        CommercialStatus commercialStatus) {

    public static UserResponse from(User user) {
        if (user instanceof Buyer buyer) {
            return new UserResponse(buyer.getIdentifier(), buyer.getFullName(), buyer.getEmail(),
                    buyer.getRole(), buyer.getStatus(), buyer.getMainAddress(),
                    buyer.getAdditionalAddresses(), buyer.getCommercialStatus());
        }
        return new UserResponse(user.getIdentifier(), user.getFullName(), user.getEmail(),
                user.getRole(), user.getStatus(), null, null, null);
    }
}
