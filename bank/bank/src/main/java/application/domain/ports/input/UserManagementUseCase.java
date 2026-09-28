package application.domain.ports.input;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.Seller;
import application.domain.models.Supervisor;
import application.domain.models.User;
import java.util.List;

/**
 * Manages the lifecycle of all system users.
 * Covers business objectives OBJ-01, OBJ-02, and OBJ-03.
 */
public interface UserManagementUseCase {

    Buyer registerBuyer(Buyer buyer);

    Seller registerSeller(Seller seller, String administratorId);

    LogisticsOperator registerLogisticsOperator(LogisticsOperator operator, String administratorId);

    Supervisor registerSupervisor(Supervisor supervisor, String administratorId);

    Administrator registerAdministrator(Administrator administrator, String requesterAdministratorId);

    User blockUser(String userId, String administratorId);

    User activateUser(String userId, String administratorId);

    User findUserById(String userId);

    User findUserByEmail(String email);

    List<User> listAllUsers(String administratorId);
}
