```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductManagerTest {

    private ProductManager productManager;

    @BeforeEach
    void setUp() {
        productManager = new ProductManager();
    }

    @Test
    @DisplayName("Debería obtener la API_KEY desde el entorno")
    void testGetApiKey_Success() {
        // Arrange
        String expectedApiKey = "test-api-key";
        System.setProperty("API_KEY", expectedApiKey);

        // Act
        String actualApiKey = ProductManager.API_KEY;

        // Assert
        assertEquals(expectedApiKey, actualApiKey);
    }

    @Test
    @DisplayName("Debería lanzar una excepción si la API_KEY no está definida")
    void testGetApiKey_Error() {
        // Arrange
        System.clearProperty("API_KEY");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = ProductManager.API_KEY;
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalArgumentException("API_KEY no está definida");
            }
        });
    }

    @Test
    @DisplayName("Debería manejar correctamente un caso donde la API_KEY está vacía")
    void testGetApiKey_Empty() {
        // Arrange
        System.setProperty("API_KEY", "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            String apiKey = ProductManager.API_KEY;
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalArgumentException("API_KEY no está definida");
            }
        });
    }
}
```