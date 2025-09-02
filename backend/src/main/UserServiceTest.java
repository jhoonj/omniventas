```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    @DisplayName("Should return formatted string when usernames are provided")
    void testProcessUserData_WithValidUsernames_ReturnsFormattedString() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2", "user3");
        String expected = "user1, user2, user3";

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    @DisplayName("Should return empty Optional when no usernames are provided")
    void testProcessUserData_WithEmptyUsernames_ReturnsEmptyOptional() {
        // Arrange
        List<String> usernames = Collections.emptyList();

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return formatted string when single username is provided")
    void testProcessUserData_WithSingleUsername_ReturnsFormattedString() {
        // Arrange
        List<String> usernames = Collections.singletonList("user1");
        String expected = "user1";

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    @DisplayName("Should handle null usernames gracefully")
    void testProcessUserData_WithNullUsernames_ReturnsEmptyOptional() {
        // Arrange
        List<String> usernames = null;

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return formatted string with duplicates")
    void testProcessUserData_WithDuplicateUsernames_ReturnsFormattedString() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user1", "user2");
        String expected = "user1, user1, user2";

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }
}
```