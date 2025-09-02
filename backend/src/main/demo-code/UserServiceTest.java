```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería retornar la contraseña codificada correctamente")
    void testGetHardcodedPassword_Success() {
        // Arrange
        String expectedPassword = "mySecretPassword"; // Simular el valor de la variable de entorno
        System.setProperty("USER_PASSWORD", expectedPassword);

        // Act
        String actualPassword = userService.getHardcodedPassword();

        // Assert
        assertEquals(expectedPassword, actualPassword);
    }

    @Test
    @DisplayName("Debería lanzar una excepción si la contraseña no está configurada")
    void testGetHardcodedPassword_NoPasswordSet() {
        // Arrange
        System.clearProperty("USER_PASSWORD");

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            userService.getHardcodedPassword();
        });

        assertEquals("USER_PASSWORD is not set", exception.getMessage());
    }

    @Test
    @DisplayName("Debería manejar correctamente la contraseña vacía")
    void testGetHardcodedPassword_EmptyPassword() {
        // Arrange
        System.setProperty("USER_PASSWORD", "");

        // Act
        String actualPassword = userService.getHardcodedPassword();

        // Assert
        assertEquals("", actualPassword);
    }
}
```