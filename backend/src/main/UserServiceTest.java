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
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).save(user);
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
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle null user data gracefully")
    void testProcessUserData_NullUser() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(null);
        });

        assertEquals("User cannot be null", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle empty email in user data")
    void testProcessUserData_EmptyEmail() {
        // Arrange
        User user = new User("", "John", "Doe");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("Email cannot be empty", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
```