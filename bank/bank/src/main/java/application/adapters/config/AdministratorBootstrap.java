package application.adapters.config;

import application.domain.models.Administrator;
import application.domain.ports.output.UserRepositoryPort;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Creates the first Administrator when the platform has none, because every other
 * administrative user must be registered by an existing Administrator.
 */
@Component
public class AdministratorBootstrap implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdministratorBootstrap.class);

    private final UserRepositoryPort userRepository;
    private final String identifier;
    private final String fullName;
    private final String email;

    public AdministratorBootstrap(UserRepositoryPort userRepository,
                                  @Value("${nexusmarket.bootstrap.admin.identifier}") String identifier,
                                  @Value("${nexusmarket.bootstrap.admin.full-name}") String fullName,
                                  @Value("${nexusmarket.bootstrap.admin.email}") String email) {
        this.userRepository = userRepository;
        this.identifier = identifier;
        this.fullName = fullName;
        this.email = email;
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean hasAdministrator = userRepository.findAll().stream()
                .anyMatch(user -> user.hasRole(SystemRole.ADMINISTRATOR));
        if (hasAdministrator) {
            return;
        }
        Administrator administrator = new Administrator();
        administrator.setIdentifier(identifier);
        administrator.setFullName(fullName);
        administrator.setEmail(email);
        administrator.setRole(SystemRole.ADMINISTRATOR);
        administrator.setStatus(UserStatus.ACTIVE);
        userRepository.save(administrator);
        LOGGER.info("Initial administrator created with identifier {}", identifier);
    }
}
