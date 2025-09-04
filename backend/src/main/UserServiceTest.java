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
    @DisplayName("Should handle edge case with null user")
    void testProcessUserData_NullUser() {
        // Arrange
        User user = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userService.processUserData(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle user not found case")
    void testProcessUserData_UserNotFound() {
        // Arrange
        User user = new User("jane.doe@example.com", "Jane", "Doe");
        when(userRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.processUserData(user));
        verify(userRepository, times(1)).findByEmail("jane.doe@example.com");
    }
}
```