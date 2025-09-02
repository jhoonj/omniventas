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
    @DisplayName("Debería procesar datos de usuario exitosamente")
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
    @DisplayName("Debería lanzar excepción cuando el usuario es nulo")
    void testProcessUserData_NullUser() {
        // Arrange
        User user = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.processUserData(user));
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el email es inválido")
    void testProcessUserData_InvalidEmail() {
        // Arrange
        User user = new User("invalid-email", "John", "Doe");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.processUserData(user));
    }

    @Test
    @DisplayName("Debería manejar correctamente la excepción de base de datos")
    void testProcessUserData_DatabaseError() {
        // Arrange
        User user = new User("john.doe@example.com", "John", "Doe");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.processUserData(user));
        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(1)).save(user);
    }
}
```