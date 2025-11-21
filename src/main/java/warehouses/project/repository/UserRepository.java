package warehouses.project.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import warehouses.project.model.User;

import java.util.Optional;

/**
 * Репозиторий для таблицы пользователей
 * Только администратор может выполнять CRUD.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Поиск пользователя по имени.
     *
     * @param username имя пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверка существования пользователя по имени.
     *
     * @param username имя пользователя
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);

    /**
     * Проверка существования пользователя по email.
     *
     * @param email email пользователя
     * @return true, если пользователь с таким email существует
     */
    boolean existsByEmail(String email);

    // Для будущей авторизации (по email)
    Optional<User> findByEmail(String email);
}
