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
        User user = new User(1L, "test@example.com", "Test User");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User result = userService.processUserData(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testProcessUserData_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.processUserData(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should handle edge case with null user ID")
    void testProcessUserData_NullUserId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.processUserData(null));
    }

    @Test
    @DisplayName("Should handle edge case with negative user ID")
    void testProcessUserData_NegativeUserId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.processUserData(-1L));
    }
}
```