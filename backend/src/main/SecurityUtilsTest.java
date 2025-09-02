```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();

    @Test
    @DisplayName("Debería incrementar loginAttempts correctamente en un entorno multihilo")
    void shouldIncrementLoginAttemptsCorrectlyInMultithreadedEnvironment() throws InterruptedException {
        // Arrange
        int numberOfThreads = 100;
        Thread[] threads = new Thread[numberOfThreads];

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(securityUtils::incrementLoginAttempts);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(numberOfThreads, securityUtils.getLoginAttempts());
    }

    @Test
    @DisplayName("Debería manejar correctamente el incremento de loginAttempts en un solo hilo")
    void shouldHandleIncrementingLoginAttemptsCorrectlyInSingleThread() {
        // Arrange
        int initialAttempts = securityUtils.getLoginAttempts();

        // Act
        securityUtils.incrementLoginAttempts();

        // Assert
        assertEquals(initialAttempts + 1, securityUtils.getLoginAttempts());
    }

    @Test
    @DisplayName("Debería no permitir que loginAttempts sea negativo")
    void shouldNotAllowNegativeLoginAttempts() {
        // Arrange
        securityUtils.setLoginAttempts(0);

        // Act
        securityUtils.decrementLoginAttempts(); // Método hipotético para decrementar

        // Assert
        assertEquals(0, securityUtils.getLoginAttempts());
    }
}
```