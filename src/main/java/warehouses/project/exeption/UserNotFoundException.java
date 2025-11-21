package warehouses.project.exeption;

// Пользователь не найден
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super("Пользователь с ID " + userId + " не найден");
    }
}
