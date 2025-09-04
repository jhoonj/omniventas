```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();

    @DisplayName("Debería incrementar el número de intentos de inicio de sesión")
    @Test
    void shouldIncrementLoginAttempts() {
        // Arrange
        int initialAttempts = securityUtils.getLoginAttempts(); // Suponiendo que hay un método para obtener los intentos

        // Act
        securityUtils.incrementLoginAttempts();

        // Assert
        assertEquals(initialAttempts + 1, securityUtils.getLoginAttempts());
    }

    @DisplayName("Debería manejar múltiples incrementos de intentos de inicio de sesión de manera segura")
    @Test
    void shouldHandleMultipleLoginAttemptsSafely() throws InterruptedException {
        // Arrange
        int initialAttempts = securityUtils.getLoginAttempts();
        Thread thread1 = new Thread(securityUtils::incrementLoginAttempts);
        Thread thread2 = new Thread(securityUtils::incrementLoginAttempts);

        // Act
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert
        assertEquals(initialAttempts + 2, securityUtils.getLoginAttempts());
    }
}
```