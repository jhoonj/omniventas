```java
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
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
    private UserRepository userRepository; // Suponiendo que hay un UserRepository

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería procesar datos de usuario correctamente")
    void testProcessUserData_Success() {
        // Arrange
        User user = new User("test@example.com", "Test User");
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
    @DisplayName("Debería lanzar excepción cuando el usuario es nulo")
    void testProcessUserData_NullUser() {
        // Arrange
        User user = null;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("User cannot be null", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el email es inválido")
    void testProcessUserData_InvalidEmail() {
        // Arrange
        User user = new User("invalid-email", "Test User");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.processUserData(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería manejar correctamente un error al guardar el usuario")
    void testProcessUserData_SaveError() {
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