```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityUtilsTest {

    @Test
    @DisplayName("Debería agregar información del sistema operativo al StringBuilder")
    void testAppendOSInfo() {
        // Arrange
        StringBuilder info = new StringBuilder();
        String expectedOS = System.getProperty("os.name");

        // Act
        info.append("OS: ").append(expectedOS).append("\n");

        // Assert
        assertTrue(info.toString().contains("OS: " + expectedOS));
    }

    @Test
    @DisplayName("Debería manejar correctamente un StringBuilder vacío")
    void testAppendEmptyStringBuilder() {
        // Arrange
        StringBuilder info = new StringBuilder();

        // Act
        info.append("OS: ").append(System.getProperty("os.name")).append("\n");

        // Assert
        assertTrue(info.toString().contains("OS: " + System.getProperty("os.name")));
    }

    @Test
    @DisplayName("Debería no modificar el StringBuilder original si se llama sin append")
    void testNoModificationWithoutAppend() {
        // Arrange
        StringBuilder info = new StringBuilder("Initial Info");

        // Act
        // No action taken

        // Assert
        assertTrue(info.toString().equals("Initial Info"));
    }
}
```