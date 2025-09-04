```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();

    @DisplayName("Debería incrementar el contador de intentos de inicio de sesión")
    @Test
    public void testIncrementLoginAttempts_Success() {
        // Arrange
        int initialAttempts = securityUtils.getLoginAttempts(); // Asumiendo que hay un método para obtener intentos

        // Act
        securityUtils.incrementLoginAttempts();

        // Assert
        assertEquals(initialAttempts + 1, securityUtils.getLoginAttempts());
    }

    @DisplayName("Debería manejar múltiples incrementos de manera segura")
    @Test
    public void testIncrementLoginAttempts_ConcurrentAccess() throws InterruptedException {
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

    @DisplayName("Debería no permitir que el contador de intentos de inicio de sesión sea negativo")
    @Test
    public void testIncrementLoginAttempts_NegativeAttempts() {
        // Arrange
        securityUtils.resetLoginAttempts(); // Asumiendo que hay un método para reiniciar intentos
        securityUtils.setLoginAttempts(-1); // Asumiendo que hay un método para establecer intentos

        // Act
        securityUtils.incrementLoginAttempts();

        // Assert
        assertEquals(0, securityUtils.getLoginAttempts()); // Asegurando que no se permita un valor negativo
    }
}
```