```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    @DisplayName("Debería procesar correctamente una lista de nombres de usuario")
    void testProcessUserData_Success() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2", "user3");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed: user1, user2, user3", result.get());
    }

    @Test
    @DisplayName("Debería retornar un Optional vacío cuando la lista de nombres de usuario está vacía")
    void testProcessUserData_EmptyList() {
        // Arrange
        List<String> usernames = Collections.emptyList();

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Debería retornar un Optional vacío cuando la lista de nombres de usuario es nula")
    void testProcessUserData_NullList() {
        // Arrange
        List<String> usernames = null;

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Debería manejar nombres de usuario con espacios en blanco")
    void testProcessUserData_WhitespaceUsernames() {
        // Arrange
        List<String> usernames = Arrays.asList("   ", "user2", "user3");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed: user2, user3", result.get());
    }

    @Test
    @DisplayName("Debería manejar nombres de usuario duplicados")
    void testProcessUserData_DuplicateUsernames() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user1", "user2");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed: user1, user2", result.get());
    }
}
```