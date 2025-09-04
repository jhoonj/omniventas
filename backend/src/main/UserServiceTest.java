```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    @DisplayName("Should return formatted string when usernames are provided")
    void shouldReturnFormattedStringWhenUsernamesAreProvided() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2", "user3");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("user1, user2, user3", result.get());
    }

    @Test
    @DisplayName("Should return empty when no usernames are provided")
    void shouldReturnEmptyWhenNoUsernamesAreProvided() {
        // Arrange
        List<String> usernames = Collections.emptyList();

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should handle null usernames gracefully")
    void shouldHandleNullUsernamesGracefully() {
        // Arrange
        List<String> usernames = null;

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return formatted string with single username")
    void shouldReturnFormattedStringWithSingleUsername() {
        // Arrange
        List<String> usernames = Collections.singletonList("user1");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("user1", result.get());
    }

    @Test
    @DisplayName("Should return formatted string with usernames containing special characters")
    void shouldReturnFormattedStringWithUsernamesContainingSpecialCharacters() {
        // Arrange
        List<String> usernames = Arrays.asList("user@1", "user#2", "user$3");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("user@1, user#2, user$3", result.get());
    }
}
```