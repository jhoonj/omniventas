```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository; // Assuming UserRepository is the repository used in UserService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should process user data successfully")
    void testProcessUserDataSuccess() {
        // Arrange
        User user = new User("test@example.com", "Test User"); // Assuming User is the entity class
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.processUserData(user);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user data is invalid")
    void testProcessUserDataInvalidUser() {
        // Arrange
        User user = new User("", ""); // Invalid user data

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("User data is invalid", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle edge case with null user")
    void testProcessUserDataNullUser() {
        // Arrange
        User user = null;

        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("User cannot be null", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle unexpected errors gracefully")
    void testProcessUserDataUnexpectedError() {
        // Arrange
        User user = new User("test@example.com", "Test User");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(1)).save(user);
    }
}
```