```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UnsafeSecurityConfigTest {

    @Mock
    private Config config; // Asumiendo que hay una clase Config que tiene el método setUsername

    @InjectMocks
    private UnsafeSecurityConfig unsafeSecurityConfig;

    public UnsafeSecurityConfigTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería establecer el nombre de usuario correctamente cuando DB_USER está configurado")
    void testSetUsername_Success() {
        // Arrange
        String dbUser = "testUser";
        System.setProperty("DB_USER", dbUser);

        // Act
        unsafeSecurityConfig.setUsernameFromEnv();

        // Assert
        verify(config).setUsername(dbUser);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void testSetUsername_ThrowsException_WhenDbUserNotConfigured() {
        // Arrange
        System.clearProperty("DB_USER");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsernameFromEnv();
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    void testSetUsername_ThrowsException_WhenDbUserIsEmpty() {
        // Arrange
        System.setProperty("DB_USER", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsernameFromEnv();
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }
}
```