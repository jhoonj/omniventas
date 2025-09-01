```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnsafeSecurityConfigTest {

    private UnsafeSecurityConfig unsafeSecurityConfig;
    private Config config; // Suponiendo que hay una clase Config que se está utilizando

    @BeforeEach
    void setUp() {
        unsafeSecurityConfig = new UnsafeSecurityConfig();
        config = mock(Config.class); // Mock de la clase Config
    }

    @Test
    @DisplayName("Debería establecer el nombre de usuario correctamente cuando DB_USER está configurado")
    void testSetUsername_Success() {
        // Arrange
        String dbUser = "testUser";
        setEnv("DB_USER", dbUser); // Método auxiliar para establecer variables de entorno

        // Act
        unsafeSecurityConfig.setUsernameFromEnv(config);

        // Assert
        verify(config).setUsername(dbUser);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void testSetUsername_ThrowsException_WhenDbUserNotConfigured() {
        // Arrange
        setEnv("DB_USER", null); // Simulando que la variable de entorno no está configurada

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsernameFromEnv(config);
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    void testSetUsername_ThrowsException_WhenDbUserIsEmpty() {
        // Arrange
        setEnv("DB_USER", ""); // Simulando que la variable de entorno está vacía

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsernameFromEnv(config);
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    // Método auxiliar para establecer variables de entorno en pruebas
    private void setEnv(String key, String value) {
        try {
            var env = System.getenv();
            var field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```