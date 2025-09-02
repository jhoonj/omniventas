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
    private UserRepository userRepository; // Asumiendo que existe un UserRepository

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
        User user = new User("", "", ""); // Invalid user data

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.processUserData(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle edge case when user already exists")
    void testProcessUserData_UserAlreadyExists() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.processUserData(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle unexpected errors gracefully")
    void testProcessUserData_UnexpectedError() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.processUserData(user));
        verify(userRepository, times(1)).save(user);
    }
}
```