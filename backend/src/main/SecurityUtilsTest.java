```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    @DisplayName("Reset login attempts should set loginAttempts to zero")
    @Test
    void testResetLoginAttempts() {
        // Arrange
        // Simulamos que hay intentos de login previos
        SecurityUtils.setLoginAttempts(5); // Método hipotético para establecer intentos

        // Act
        SecurityUtils.resetLoginAttempts();

        // Assert
        assertEquals(0, SecurityUtils.getLoginAttempts()); // Método hipotético para obtener intentos
    }
}
```