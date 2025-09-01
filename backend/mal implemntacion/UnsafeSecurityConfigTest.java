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
    private Config config; // Suponiendo que hay una clase Config que tiene el método setUsername

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
        System.setProperty("DB_USER", expectedDbUser);

        // Act
        unsafeSecurityConfig.setDbUser();

        // Assert
        verify(config).setUsername(expectedDbUser);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void shouldThrowExceptionWhenDbUserIsNotConfigured() {
        // Arrange
        System.clearProperty("DB_USER");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setDbUser();
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso cuando DB_USER está vacío")
    void shouldThrowExceptionWhenDbUserIsEmpty() {
        // Arrange
        System.setProperty("DB_USER", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setDbUser();
        });
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }
}
```