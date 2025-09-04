```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository; // Asumiendo que hay un repositorio para manejar usuarios

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
        User user = new User(username, expectedEmail); // Asumiendo que hay una clase User

        when(userRepository.findByUsername(username)).thenReturn(user);

        // Act
        String actualEmail = userService.getUserEmail(username);

        // Assert
        assertEquals(expectedEmail, actualEmail);
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Debería lanzar una excepción cuando el usuario no existe")
    public void testGetUserEmail_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserEmail(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Debería manejar correctamente un nombre de usuario nulo")
    public void testGetUserEmail_NullUsername() {
        // Arrange
        String username = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserEmail(username));
    }

    @Test
    @DisplayName("Debería manejar correctamente un nombre de usuario vacío")
    public void testGetUserEmail_EmptyUsername() {
        // Arrange
        String username = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserEmail(username));
    }
}
```