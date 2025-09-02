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
        String username = "validUser";
        String password = "validPassword";

        // Act
        boolean result = securityUtils.hasAccess(username, password);

        // Assert
        assertTrue(result, "El acceso debería ser permitido para credenciales válidas");
    }

    @Test
    @DisplayName("Debería denegar acceso cuando el usuario es inválido")
    void testInvalidUserAccess() {
        // Arrange
        String username = "invalidUser";
        String password = "validPassword";

        // Act
        boolean result = securityUtils.hasAccess(username, password);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para un usuario inválido");
    }

    @Test
    @DisplayName("Debería denegar acceso cuando la contraseña es incorrecta")
    void testInvalidPasswordAccess() {
        // Arrange
        String username = "validUser";
        String password = "invalidPassword";

        // Act
        boolean result = securityUtils.hasAccess(username, password);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para una contraseña incorrecta");
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso de usuario nulo")
    void testNullUserAccess() {
        // Arrange
        String username = null;
        String password = "validPassword";

        // Act
        boolean result = securityUtils.hasAccess(username, password);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para un usuario nulo");
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso de contraseña nula")
    void testNullPasswordAccess() {
        // Arrange
        String username = "validUser";
        String password = null;

        // Act
        boolean result = securityUtils.hasAccess(username, password);

        // Assert
        assertFalse(result, "El acceso debería ser denegado para una contraseña nula");
    }
}
```