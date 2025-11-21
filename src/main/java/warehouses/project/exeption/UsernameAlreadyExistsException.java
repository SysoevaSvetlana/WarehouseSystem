package warehouses.project.exeption;

// Дублирующий username
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Пользователь с именем '" + username + "' уже существует");
    }
}
