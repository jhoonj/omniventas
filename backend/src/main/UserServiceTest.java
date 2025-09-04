```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository; // Assuming there's a UserRepository

    @BeforeEach
    void setUp() {
        // Setup code if needed
    }

    @Test
    @DisplayName("Should process user data successfully")
    void testProcessUserDataSuccess() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.processUserData(user);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user data is invalid")
    void testProcessUserDataInvalid() {
        // Arrange
        User user = new User("", "John", "Doe"); // Invalid email

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("Invalid user data", exception.getMessage());
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
    @DisplayName("Should handle edge case with empty user")
    void testProcessUserDataEmptyUser() {
        // Arrange
        User user = new User("", "", ""); // All fields empty

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("Invalid user data", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}
```