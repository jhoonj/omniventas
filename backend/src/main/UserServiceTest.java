```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository; // Suponiendo que hay un repositorio de usuarios

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería retornar el email del usuario dado un nombre de usuario válido")
    public void testGetUserEmail_Success() {
        // Arrange
        String username = "validUser";
        String expectedEmail = "validUser@example.com";
        when(userRepository.findEmailByUsername(username)).thenReturn(expectedEmail);

        // Act
        String actualEmail = userService.getUserEmail(username);

        // Assert
        assertEquals(expectedEmail, actualEmail);
        verify(userRepository, times(1)).findEmailByUsername(username);
    }

    @Test
    @DisplayName("Debería lanzar una excepción cuando el nombre de usuario no existe")
    public void testGetUserEmail_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        when(userRepository.findEmailByUsername(username)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserEmail(username);
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(userRepository, times(1)).findEmailByUsername(username);
    }

    @Test
    @DisplayName("Debería lanzar una excepción cuando el nombre de usuario es nulo")
    public void testGetUserEmail_NullUsername() {
        // Arrange
        String username = null;

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserEmail(username);
        });

        assertEquals("El nombre de usuario no puede ser nulo", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar una excepción cuando el nombre de usuario está vacío")
    public void testGetUserEmail_EmptyUsername() {
        // Arrange
        String username = "";

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.getUserEmail(username);
        });

        assertEquals("El nombre de usuario no puede estar vacío", exception.getMessage());
    }
}
```