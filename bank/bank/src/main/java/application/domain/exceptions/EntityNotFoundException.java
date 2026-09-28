package application.domain.exceptions;

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityName, String identifier) {
        super(entityName + " not found with identifier: " + identifier);
    }
}
