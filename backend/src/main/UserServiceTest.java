```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("UserService Test")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository; // Assuming there's a UserRepository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return formatted string for valid user data")
    void testProcessUserData_Success() {
        // Arrange
        User user = new User("John", "Doe", "john.doe@example.com");
        when(userRepository.findUserById(1)).thenReturn(Optional.of(user));

        // Act
        String result = userService.processUserData(1);

        // Assert
        assertEquals("User: John Doe, Email: john.doe@example.com", result);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testProcessUserData_UserNotFound() {
        // Arrange
        when(userRepository.findUserById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.processUserData(1);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle edge case with null user ID")
    void testProcessUserData_NullUserId() {
        // Arrange
        Integer userId = null;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(userId);
        });
        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle edge case with negative user ID")
    void testProcessUserData_NegativeUserId() {
        // Arrange
        int userId = -1;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(userId);
        });
        assertEquals("User ID must be positive", exception.getMessage());
    }
}
```