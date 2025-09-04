```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

@DisplayName("UserService Test")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository; // Assuming UserRepository is the repository interface

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should process user data successfully")
    void testProcessUserData_Success() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        User processedUser = userService.processUserData(user);

        // Assert
        assertNotNull(processedUser);
        assertEquals("John", processedUser.getFirstName());
        assertEquals("Doe", processedUser.getLastName());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testProcessUserData_UserNotFound() {
        // Arrange
        User user = new User("unknown@example.com", "Unknown", "User");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should handle edge case with null user")
    void testProcessUserData_NullUser() {
        // Arrange
        User user = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(user);
        });
    }

    @Test
    @DisplayName("Should handle edge case with invalid email format")
    void testProcessUserData_InvalidEmailFormat() {
        // Arrange
        User user = new User("invalid-email", "John", "Doe");

        // Act & Assert
        assertThrows(InvalidEmailFormatException.class, () -> {
            userService.processUserData(user);
        });
    }
}
```