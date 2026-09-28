package application.domain.exceptions;

public class DuplicateIdentifierException extends DomainException {
    public DuplicateIdentifierException(String identifier) {
        super("A user with identifier '" + identifier + "' is already registered.");
    }
}
