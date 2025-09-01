```java
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthErrorTest {

    @Test
    @DisplayName("Debería lanzar AuthError con mensaje 'Invalid email format'")
    void shouldThrowAuthErrorWithInvalidEmailFormatMessage() {
        // Arrange
        String expectedMessage = "Invalid email format";

        // Act & Assert
        AuthError exception = assertThrows(AuthError.class, () -> {
            throw new AuthError(expectedMessage);
        });

        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertEquals("AuthError", exception.getName());
    }

    @Test
    @DisplayName("Debería lanzar AuthError con mensaje personalizado")
    void shouldThrowAuthErrorWithCustomMessage() {
        // Arrange
        String customMessage = "Custom error message";

        // Act & Assert
        AuthError exception = assertThrows(AuthError.class, () -> {
            throw new AuthError(customMessage);
        });

        // Assert
        assertEquals(customMessage, exception.getMessage());
        assertEquals("AuthError", exception.getName());
    }

    @Test
    @DisplayName("Debería lanzar AuthError al recibir un mensaje nulo")
    void shouldThrowAuthErrorWithNullMessage() {
        // Arrange
        String nullMessage = null;

        // Act & Assert
        AuthError exception = assertThrows(AuthError.class, () -> {
            throw new AuthError(nullMessage);
        });

        // Assert
        assertNull(exception.getMessage());
        assertEquals("AuthError", exception.getName());
    }
}
```