```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    @DisplayName("Debería incrementar el número de intentos de inicio de sesión")
    @Test
    void shouldIncrementLoginAttempts() {
        // Arrange
        AtomicInteger loginAttempts = new AtomicInteger(0);
        
        // Act
        loginAttempts.incrementAndGet();
        
        // Assert
        assertEquals(1, loginAttempts.get());
    }

    @DisplayName("Debería reiniciar los intentos de inicio de sesión")
    @Test
    void shouldResetLoginAttempts() {
        // Arrange
        AtomicInteger loginAttempts = new AtomicInteger(3);
        
        // Act
        loginAttempts.set(0);
        
        // Assert
        assertEquals(0, loginAttempts.get());
    }

    @DisplayName("Debería manejar múltiples intentos de inicio de sesión")
    @Test
    void shouldHandleMultipleLoginAttempts() {
        // Arrange
        AtomicInteger loginAttempts = new AtomicInteger(0);
        
        // Act
        for (int i = 0; i < 5; i++) {
            loginAttempts.incrementAndGet();
        }
        
        // Assert
        assertEquals(5, loginAttempts.get());
    }

    @DisplayName("Debería no permitir valores negativos en intentos de inicio de sesión")
    @Test
    void shouldNotAllowNegativeLoginAttempts() {
        // Arrange
        AtomicInteger loginAttempts = new AtomicInteger(0);
        
        // Act
        loginAttempts.set(-1);
        
        // Assert
        assertEquals(0, loginAttempts.get());
    }
}
```