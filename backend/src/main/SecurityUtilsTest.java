```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SecurityUtilsTest {

    @InjectMocks
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería permitir acceso cuando las credenciales son válidas")
    void testValidAccess() {
        // Arrange
        String validUsername = "user";
        String validPassword = "password";

        // Act
        boolean result = securityUtils.hasAccess(validUsername, validPassword);

        // Assert
        assertTrue(result, "El acceso debería ser permitido para credenciales válidas");
    }

    @Test
    @DisplayName("Debería denegar acceso cuando el nombre de usuario es inválido")
    void testInvalidUsername() {
        // Arrange
        String invalidUsername = "invalidUser";
        String validPassword = "password";

        // Act
        boolean result = securityUtils.hasAccess(invalidUsername, validPassword);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para un nombre de usuario inválido");
    }

    @Test
    @DisplayName("Debería denegar acceso cuando la contraseña es inválida")
    void testInvalidPassword() {
        // Arrange
        String validUsername = "user";
        String invalidPassword = "invalidPassword";

        // Act
        boolean result = securityUtils.hasAccess(validUsername, invalidPassword);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para una contraseña inválida");
    }

    @Test
    @DisplayName("Debería denegar acceso cuando ambos, nombre de usuario y contraseña son inválidos")
    void testInvalidCredentials() {
        // Arrange
        String invalidUsername = "invalidUser";
        String invalidPassword = "invalidPassword";

        // Act
        boolean result = securityUtils.hasAccess(invalidUsername, invalidPassword);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para credenciales inválidas");
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso de credenciales nulas")
    void testNullCredentials() {
        // Arrange
        String nullUsername = null;
        String nullPassword = null;

        // Act
        boolean result = securityUtils.hasAccess(nullUsername, nullPassword);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para credenciales nulas");
    }
}
```