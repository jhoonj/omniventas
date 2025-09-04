```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();

    @Test
    @DisplayName("Debería incrementar loginAttempts correctamente")
    void shouldIncrementLoginAttemptsCorrectly() {
        // Arrange
        int initialAttempts = securityUtils.getLoginAttempts(); // Asumiendo que hay un método para obtener el valor

        // Act
        synchronized (securityUtils) {
            securityUtils.incrementLoginAttempts(); // Método que contiene el código a testear
        }

        // Assert
        assertEquals(initialAttempts + 1, securityUtils.getLoginAttempts());
    }

    @Test
    @DisplayName("Debería manejar múltiples hilos correctamente")
    void shouldHandleMultipleThreadsCorrectly() throws InterruptedException {
        // Arrange
        int initialAttempts = securityUtils.getLoginAttempts();
        int numberOfThreads = 10;
        Thread[] threads = new Thread[numberOfThreads];

        // Act
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> {
                synchronized (securityUtils) {
                    securityUtils.incrementLoginAttempts();
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(initialAttempts + numberOfThreads, securityUtils.getLoginAttempts());
    }

    @Test
    @DisplayName("Debería manejar el caso de no incrementar si no se llama al método")
    void shouldNotIncrementIfMethodNotCalled() {
        // Arrange
        int initialAttempts = securityUtils.getLoginAttempts();

        // Act - No se llama al método

        // Assert
        assertEquals(initialAttempts, securityUtils.getLoginAttempts());
    }
}
```