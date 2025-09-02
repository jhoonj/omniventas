```java
package backend.src.main.demo-code;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtilsTest {

    @Test
    @DisplayName("Debería generar un hash SHA-256 exitosamente")
    public void testGenerateHashSHA256_Success() throws NoSuchAlgorithmException {
        // Arrange
        String input = "testInput";
        String expectedHash = "6f1ed002ab5595859014ebf0951522d9"; // Hash de "testInput" en SHA-256

        // Act
        String actualHash = SecurityUtils.generateHashSHA256(input);

        // Assert
        Assertions.assertEquals(expectedHash, actualHash);
    }

    @Test
    @DisplayName("Debería lanzar excepción al usar algoritmo no soportado")
    public void testGenerateHashSHA256_NoSuchAlgorithmException() {
        // Arrange
        String input = "testInput";

        // Act & Assert
        Assertions.assertThrows(NoSuchAlgorithmException.class, () -> {
            SecurityUtils.generateHashSHA256WithUnsupportedAlgorithm(input);
        });
    }

    @Test
    @DisplayName("Debería manejar entrada nula correctamente")
    public void testGenerateHashSHA256_NullInput() {
        // Arrange
        String input = null;

        // Act
        String actualHash = SecurityUtils.generateHashSHA256(input);

        // Assert
        Assertions.assertNull(actualHash);
    }

    @Test
    @DisplayName("Debería manejar entrada vacía correctamente")
    public void testGenerateHashSHA256_EmptyInput() {
        // Arrange
        String input = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // Hash de "" en SHA-256

        // Act
        String actualHash = SecurityUtils.generateHashSHA256(input);

        // Assert
        Assertions.assertEquals(expectedHash, actualHash);
    }
}
```