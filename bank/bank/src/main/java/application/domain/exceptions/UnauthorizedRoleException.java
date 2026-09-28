package application.domain.exceptions;

public class UnauthorizedRoleException extends DomainException {
    public UnauthorizedRoleException(String action, String requiredRole) {
        super("Operation '" + action + "' requires role: " + requiredRole);
    }
}
