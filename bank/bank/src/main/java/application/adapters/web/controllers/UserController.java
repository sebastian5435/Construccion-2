package application.adapters.web.controllers;

import application.adapters.web.RequestHeaders;
import application.adapters.web.dto.request.BuyerRegistrationRequest;
import application.adapters.web.dto.request.SellerRegistrationRequest;
import application.adapters.web.dto.request.UserRegistrationRequest;
import application.adapters.web.dto.response.UserResponse;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.ports.input.UserManagementUseCase;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserManagementUseCase userManagement;

    public UserController(UserManagementUseCase userManagement) {
        this.userManagement = userManagement;
    }

    @PostMapping("/buyers")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerBuyer(@Valid @RequestBody BuyerRegistrationRequest request) {
        Buyer buyer = fill(new Buyer(), request.identifier(), request.fullName(), request.email());
        buyer.setMainAddress(request.mainAddress());
        buyer.setAdditionalAddresses(request.additionalAddresses() == null
                ? new ArrayList<>()
                : new ArrayList<>(request.additionalAddresses()));
        return UserResponse.from(userManagement.registerBuyer(buyer));
    }

    @PostMapping("/sellers")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerSeller(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                       @Valid @RequestBody SellerRegistrationRequest request) {
        Seller seller = fill(new Seller(), request.identifier(), request.fullName(), request.email());
        Warehouse firstWarehouse = new Warehouse();
        firstWarehouse.setName(request.firstWarehouse().name());
        firstWarehouse.setAddress(request.firstWarehouse().address());
        return UserResponse.from(userManagement.registerSeller(seller, firstWarehouse, requesterId));
    }

    @PostMapping("/logistics-operators")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerLogisticsOperator(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                                  @Valid @RequestBody UserRegistrationRequest request) {
        LogisticsOperator operator = fill(new LogisticsOperator(),
                request.identifier(), request.fullName(), request.email());
        return UserResponse.from(userManagement.registerLogisticsOperator(operator, requesterId));
    }

    @PostMapping("/supervisors")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerSupervisor(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                           @Valid @RequestBody UserRegistrationRequest request) {
        Supervisor supervisor = fill(new Supervisor(), request.identifier(), request.fullName(), request.email());
        return UserResponse.from(userManagement.registerSupervisor(supervisor, requesterId));
    }

    @PostMapping("/administrators")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerAdministrator(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                              @Valid @RequestBody UserRegistrationRequest request) {
        Administrator administrator = fill(new Administrator(),
                request.identifier(), request.fullName(), request.email());
        return UserResponse.from(userManagement.registerAdministrator(administrator, requesterId));
    }

    @PatchMapping("/users/{userId}/block")
    public UserResponse blockUser(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                  @PathVariable String userId) {
        return UserResponse.from(userManagement.blockUser(userId, requesterId));
    }

    @PatchMapping("/users/{userId}/activate")
    public UserResponse activateUser(@RequestHeader(RequestHeaders.USER_ID) String requesterId,
                                     @PathVariable String userId) {
        return UserResponse.from(userManagement.activateUser(userId, requesterId));
    }

    @GetMapping("/users/{userId}")
    public UserResponse findUserById(@PathVariable String userId) {
        return UserResponse.from(userManagement.findUserById(userId));
    }

    @GetMapping("/users/search")
    public UserResponse findUserByEmail(@RequestParam String email) {
        return UserResponse.from(userManagement.findUserByEmail(email));
    }

    @GetMapping("/users")
    public List<UserResponse> listAllUsers(@RequestHeader(RequestHeaders.USER_ID) String requesterId) {
        return userManagement.listAllUsers(requesterId).stream().map(UserResponse::from).toList();
    }

    private static <T extends User> T fill(T user, String identifier, String fullName, String email) {
        user.setIdentifier(identifier);
        user.setFullName(fullName);
        user.setEmail(email);
        return user;
    }
}
