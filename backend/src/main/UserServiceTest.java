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

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should process user data successfully")
    void testProcessUserData_Success() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.processUserData(user);

        // Assert
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void testProcessUserData_UserAlreadyExists() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle null user data gracefully")
    void testProcessUserData_NullUser() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(null);
        });

        assertEquals("User data cannot be null", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle invalid email format")
    void testProcessUserData_InvalidEmail() {
        // Arrange
        User user = new User("invalid-email", "John", "Doe");

        // Act & Assert
        Exception exception = assertThrows(InvalidEmailFormatException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
```