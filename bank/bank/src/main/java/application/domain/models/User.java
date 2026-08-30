package application.domain.models;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

public abstract class User {
    private String identifier;
    private String fullName;
    private String email;
    private SystemRole role;
    private UserStatus status;

    public User() {}
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public SystemRole getRole() { return role; }
    public void setRole(SystemRole role) { this.role = role; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}