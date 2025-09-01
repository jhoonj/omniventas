```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class UnsafeSecurityConfigTest {

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER no está configurado")
    public void testDbUserNotConfigured() {
        // Arrange
        System.clearProperty("DB_USER");

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String dbUser = System.getenv("DB_USER");
            if (dbUser == null || dbUser.isEmpty()) {
                throw new IllegalArgumentException("DB_USER no está configurado");
            }
        });

        Assertions.assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería lanzar IllegalArgumentException cuando DB_USER está vacío")
    public void testDbUserIsEmpty() {
        // Arrange
        System.setProperty("DB_USER", "");

        // Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            String dbUser = System.getenv("DB_USER");
            if (dbUser == null || dbUser.isEmpty()) {
                throw new IllegalArgumentException("DB_USER no está configurado");
            }
        });

        Assertions.assertEquals("DB_USER no está configurado", exception.getMessage());
    }

    @Test
    @DisplayName("Debería retornar el valor de DB_USER cuando está configurado")
    public void testDbUserIsConfigured() {
        // Arrange
        String expectedDbUser = "testUser";
        System.setProperty("DB_USER", expectedDbUser);

        // Act
        String dbUser = System.getenv("DB_USER");

        // Assert
        Assertions.assertEquals(expectedDbUser, dbUser);
    }
}
```