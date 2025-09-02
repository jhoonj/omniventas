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
    @DisplayName("Should return formatted string when usernames are provided")
    void testProcessUserData_Success() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2", "user3");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed users: user1, user2, user3", result.get());
    }

    @Test
    @DisplayName("Should return empty when no usernames are provided")
    void testProcessUserData_EmptyList() {
        // Arrange
        List<String> usernames = Collections.emptyList();

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should handle null usernames gracefully")
    void testProcessUserData_NullList() {
        // Arrange
        List<String> usernames = null;

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return formatted string with single username")
    void testProcessUserData_SingleUsername() {
        // Arrange
        List<String> usernames = Collections.singletonList("user1");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed users: user1", result.get());
    }

    @Test
    @DisplayName("Should return formatted string with multiple usernames")
    void testProcessUserData_MultipleUsernames() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed users: user1, user2", result.get());
    }
}
```