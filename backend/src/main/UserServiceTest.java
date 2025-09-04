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

@DisplayName("UserServiceTest")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should process user data successfully")
    void testProcessUserData_Success() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.processUserData(user);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user data is invalid")
    void testProcessUserData_InvalidUser() {
        // Arrange
        User user = new User("", "John", "Doe"); // Invalid email

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.processUserData(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle user not found scenario")
    void testProcessUserData_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.processUserData(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Should handle edge case with null user")
    void testProcessUserData_NullUser() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> userService.processUserData(null));
        verify(userRepository, never()).save(any(User.class));
    }
}
```