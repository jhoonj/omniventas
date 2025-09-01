```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

public class UnsafeSecurityConfigTest {

    @InjectMocks
    private UnsafeSecurityConfig config;

    @Mock
    private Environment env;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería establecer el nombre de usuario correctamente desde el entorno")
    public void testSetUsername_Success() {
        // Arrange
        String expectedUsername = "testUser";
        when(env.getProperty("DB_USER", "defaultUser")).thenReturn(expectedUsername);

        // Act
        config.setUsername(env.getProperty("DB_USER", "defaultUser"));

        // Assert
        assertEquals(expectedUsername, config.getUsername());
    }

    @Test
    @DisplayName("Debería establecer el nombre de usuario por defecto cuando no se proporciona")
    public void testSetUsername_DefaultValue() {
        // Arrange
        String defaultUsername = "defaultUser";
        when(env.getProperty("DB_USER", "defaultUser")).thenReturn(null);

        // Act
        config.setUsername(env.getProperty("DB_USER", "defaultUser"));

        // Assert
        assertEquals(defaultUsername, config.getUsername());
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso cuando el entorno no tiene la propiedad")
    public void testSetUsername_EmptyProperty() {
        // Arrange
        String expectedUsername = "defaultUser";
        when(env.getProperty("DB_USER", "defaultUser")).thenReturn("");

        // Act
        config.setUsername(env.getProperty("DB_USER", "defaultUser"));

        // Assert
        assertEquals(expectedUsername, config.getUsername());
    }
}
```