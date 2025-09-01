```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnsafeSecurityConfigTest {

    private UnsafeSecurityConfig unsafeSecurityConfig;
    private Config mockConfig;

    @BeforeEach
    void setUp() {
        unsafeSecurityConfig = new UnsafeSecurityConfig();
        mockConfig = mock(Config.class);
    }

    @Test
    @DisplayName("Debería establecer el nombre de usuario cuando DB_USER está configurado")
    void testSetUsernameWhenDbUserIsConfigured() {
        // Arrange
        String dbUser = "testUser";
        setEnv("DB_USER", dbUser);

        // Act
        unsafeSecurityConfig.setUsername(mockConfig);

        // Assert
        verify(mockConfig).setUsername(dbUser);
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void testSetUsernameWhenDbUserIsNotConfigured() {
        // Arrange
        setEnv("DB_USER", null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsername(mockConfig);
        });

        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    void testSetUsernameWhenDbUserIsEmpty() {
        // Arrange
        setEnv("DB_USER", "");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            unsafeSecurityConfig.setUsername(mockConfig);
        });

        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    private void setEnv(String key, String value) {
        try {
            var env = System.getenv();
            var field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set environment variable", e);
        }
    }
}
```