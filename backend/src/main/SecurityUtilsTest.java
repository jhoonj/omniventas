```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    @DisplayName("Debería reiniciar los intentos de inicio de sesión a cero")
    @Test
    void testResetLoginAttempts() {
        // Arrange
        SecurityUtils.setLoginAttempts(5); // Suponiendo que hay un método para establecer intentos
        assertEquals(5, SecurityUtils.getLoginAttempts()); // Verificamos que los intentos son 5 antes de resetear

        // Act
        SecurityUtils.resetLoginAttempts();

        // Assert
        assertEquals(0, SecurityUtils.getLoginAttempts()); // Verificamos que los intentos son 0 después de resetear
    }
}
```