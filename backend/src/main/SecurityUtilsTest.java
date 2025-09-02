```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SecurityUtils Test")
public class SecurityUtilsTest {

    @Test
    @DisplayName("Debería incrementar el contador de intentos de inicio de sesión")
    public void testIncrementLoginAttempts() {
        // Arrange
        AtomicInteger initialAttempts = new AtomicInteger(SecurityUtils.getLoginAttempts());

        // Act
        SecurityUtils.incrementLoginAttempts();

        // Assert
        assertEquals(initialAttempts.get() + 1, SecurityUtils.getLoginAttempts());
    }

    @Test
    @DisplayName("Debería resetear el contador de intentos de inicio de sesión")
    public void testResetLoginAttempts() {
        // Arrange
        SecurityUtils.incrementLoginAttempts();
        SecurityUtils.incrementLoginAttempts();
        int attemptsBeforeReset = SecurityUtils.getLoginAttempts();

        // Act
        SecurityUtils.resetLoginAttempts();

        // Assert
        assertEquals(0, SecurityUtils.getLoginAttempts());
        assertEquals(attemptsBeforeReset, 2);
    }

    @Test
    @DisplayName("Debería devolver el número actual de intentos de inicio de sesión")
    public void testGetLoginAttempts() {
        // Arrange
        SecurityUtils.incrementLoginAttempts();
        SecurityUtils.incrementLoginAttempts();

        // Act
        int currentAttempts = SecurityUtils.getLoginAttempts();

        // Assert
        assertEquals(2, currentAttempts);
    }

    @Test
    @DisplayName("Debería manejar correctamente el caso de intentos de inicio de sesión sin incrementos previos")
    public void testGetLoginAttemptsWithoutIncrement() {
        // Act
        int currentAttempts = SecurityUtils.getLoginAttempts();

        // Assert
        assertEquals(0, currentAttempts);
    }
}
```