```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Pruebas unitarias para la clase SecurityUtils")
class SecurityUtilsTest {

    private final SecurityUtils securityUtils = new SecurityUtils();

    @Test
    @DisplayName("Debería retornar la información restringida correctamente")
    void testGetSystemInfo_ReturnsRestrictedInfo() {
        // Arrange
        String expectedInfo = "Información restringida";

        // Act
        String actualInfo = securityUtils.getSystemInfo();

        // Assert
        assertEquals(expectedInfo, actualInfo, "El método getSystemInfo debería retornar 'Información restringida'");
    }
}
```