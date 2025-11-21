package warehouses.project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import warehouses.project.model.Role;
import warehouses.project.model.User;
import warehouses.project.repository.UserRepository;

/**
 * Инициализатор данных при старте приложения.
 * Создает администратора по умолчанию, если его еще нет в базе.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
    }

    /**
     * Создает администратора по умолчанию, если его еще нет.
     */
    private void createDefaultAdmin() {
        String adminUsername = "admin";
        String adminEmail = "admin@warehouse.com";
        String adminPassword = "admin123";

        // Проверяем, существует ли уже администратор
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);
            log.info("✅ Создан администратор по умолчанию:");
            log.info("   Username: {}", adminUsername);
            log.info("   Email: {}", adminEmail);
            log.info("   Password: {}", adminPassword);
            log.info("   Role: {}", Role.ROLE_ADMIN);
        } else {
            log.info("ℹ️  Администратор '{}' уже существует в базе данных", adminUsername);
        }
    }
}

