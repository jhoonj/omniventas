```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UnsafeSecurityConfigTest {

    @Mock
    private Config config; // Suponiendo que 'config' es un objeto que tiene el método setUsername

    @InjectMocks
    private UnsafeSecurityConfig unsafeSecurityConfig;

    public UnsafeSecurityConfigTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería establecer el nombre de usuario correctamente cuando DB_USER está configurado")
    void shouldSetUsernameWhenDbUserIsConfigured() {
        // Arrange
        String expectedDbUser = "testUser";
        setEnv("DB_USER", expectedDbUser); // Método auxiliar para establecer variables de entorno

        // Act
        unsafeSecurityConfig.setDbUser();

        // Assert
        verify(config).setUsername(expectedDbUser);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void shouldThrowExceptionWhenDbUserIsNotConfigured() {
        // Arrange
        setEnv("DB_USER", null); // Simulando que la variable de entorno no está configurada

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setDbUser();
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    void shouldThrowExceptionWhenDbUserIsEmpty() {
        // Arrange
        setEnv("DB_USER", ""); // Simulando que la variable de entorno está vacía

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setDbUser();
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