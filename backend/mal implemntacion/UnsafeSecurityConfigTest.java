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
        setEnv("DB_USER", dbUser); // Método para establecer variable de entorno

        // Act
        unsafeSecurityConfig.setUsername(config);

        // Assert
        verify(config).setUsername(dbUser);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void testSetUsername_ThrowsException_WhenDbUserNotConfigured() {
        // Arrange
        clearEnv("DB_USER"); // Método para limpiar variable de entorno

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsername(config);
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    void testSetUsername_ThrowsException_WhenDbUserIsEmpty() {
        // Arrange
        setEnv("DB_USER", ""); // Establecer DB_USER como vacío

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsername(config);
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    // Métodos auxiliares para manejar variables de entorno en pruebas
    private void setEnv(String key, String value) {
        // Implementación para establecer variables de entorno en pruebas
        // Esto puede variar dependiendo del entorno de pruebas
        System.setProperty(key, value);
    }

    private void clearEnv(String key) {
        // Implementación para limpiar variables de entorno en pruebas
        System.clearProperty(key);
    }
}
```