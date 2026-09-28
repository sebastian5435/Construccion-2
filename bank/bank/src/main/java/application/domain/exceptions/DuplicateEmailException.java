package application.domain.exceptions;

public class DuplicateEmailException extends DomainException {
    public DuplicateEmailException(String email) {
        super("A user with the email '" + email + "' is already registered.");
    }
}
