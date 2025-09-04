```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserService Test")
class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    @DisplayName("Debería procesar datos de usuario correctamente")
    void shouldProcessUserDataSuccessfully() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2", "user3");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed: user1, user2, user3", result.get());
    }

    @Test
    @DisplayName("Debería retornar vacío cuando la lista de usuarios está vacía")
    void shouldReturnEmptyWhenUsernamesListIsEmpty() {
        // Arrange
        List<String> usernames = Collections.emptyList();

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería retornar vacío cuando la lista de usuarios es nula")
    void shouldReturnEmptyWhenUsernamesListIsNull() {
        // Arrange
        List<String> usernames = null;

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debería manejar nombres de usuario con espacios en blanco")
    void shouldHandleUsernamesWithWhitespace() {
        // Arrange
        List<String> usernames = Arrays.asList(" user1 ", " user2 ", " user3 ");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed: user1, user2, user3", result.get());
    }
}
```