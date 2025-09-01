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
    void testSetUsernameWithValidDbUser() {
        // Arrange
        String expectedDbUser = "validUser";
        setEnv("DB_USER", expectedDbUser); // Método para establecer variable de entorno

        // Act
        unsafeSecurityConfig.setUsernameFromEnv(config);

        // Assert
        verify(config).setUsername(expectedDbUser);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void testSetUsernameWithNullDbUser() {
        // Arrange
        setEnv("DB_USER", null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsernameFromEnv(config);
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    void testSetUsernameWithEmptyDbUser() {
        // Arrange
        setEnv("DB_USER", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsernameFromEnv(config);
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    // Método auxiliar para establecer variables de entorno en pruebas
    private void setEnv(String key, String value) {
        try {
            var field = System.getenv().getClass().getDeclaredField("m");
            field.setAccessible(true);
            var env = (Map<String, String>) field.get(System.getenv());
            env.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```