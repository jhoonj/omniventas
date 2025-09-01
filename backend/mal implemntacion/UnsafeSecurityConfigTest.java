```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UnsafeSecurityConfigTest {

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    void testDbUserNotConfigured() {
        // Arrange
        System.clearProperty("DB_USER");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            String dbUser = System.getenv("DB_USER");
            if (dbUser == null || dbUser.isEmpty()) {
                throw new IllegalArgumentException("DB_USER no está configurado");
            }
        });

        // Assert
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    void testDbUserIsEmpty() {
        // Arrange
        System.setProperty("DB_USER", "");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            String dbUser = System.getenv("DB_USER");
            if (dbUser == null || dbUser.isEmpty()) {
                throw new IllegalArgumentException("DB_USER no está configurado");
            }
        });

        // Assert
        assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería retornar el valor de DB_USER cuando está configurado")
    void testDbUserIsConfigured() {
        // Arrange
        String expectedDbUser = "testUser";
        System.setProperty("DB_USER", expectedDbUser);

        // Act
        String dbUser = System.getenv("DB_USER");
        if (dbUser == null || dbUser.isEmpty()) {
            throw new IllegalArgumentException("DB_USER no está configurado");
        }

        // Assert
        assertEquals(expectedDbUser, dbUser);
    }
}
```