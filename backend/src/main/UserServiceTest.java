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
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should process user data successfully")
    void testProcessUserData_Success() {
        // Arrange
        User user = new User(1, "John Doe", "john.doe@example.com");
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        User result = userService.processUserData(1);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testProcessUserData_UserNotFound() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.processUserData(1));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should handle invalid user ID gracefully")
    void testProcessUserData_InvalidUserId() {
        // Arrange
        int invalidUserId = -1;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.processUserData(invalidUserId));
    }

    @Test
    @DisplayName("Should process user data with edge case")
    void testProcessUserData_EdgeCase() {
        // Arrange
        User user = new User(2, "Jane Doe", "jane.doe@example.com");
        when(userRepository.findById(2)).thenReturn(Optional.of(user));

        // Act
        User result = userService.processUserData(2);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        verify(userRepository, times(1)).findById(2);
    }
}
```