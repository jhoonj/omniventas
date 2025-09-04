```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias para la clase SecurityUtils")
class SecurityUtilsTest {

    @Test
    @DisplayName("Debería eliminar el acceso de backdoor correctamente")
    void testRemoveBackdoorAccess_Success() {
        // Arrange
        SecurityUtils securityUtils = new SecurityUtils();
        // Suponiendo que hay un método para agregar acceso de backdoor
        securityUtils.addBackdoorAccess("user1");

        // Act
        boolean result = securityUtils.removeBackdoorAccess("user1");

        // Assert
        assertTrue(result, "El acceso de backdoor debería ser eliminado exitosamente.");
    }

    @Test
    @DisplayName("Debería retornar false al intentar eliminar un acceso de backdoor que no existe")
    void testRemoveBackdoorAccess_NonExistent() {
        // Arrange
        SecurityUtils securityUtils = new SecurityUtils();

        // Act
        boolean result = securityUtils.removeBackdoorAccess("nonExistentUser");

        // Assert
        assertFalse(result, "Debería retornar false al intentar eliminar un acceso que no existe.");
    }

    @Test
    @DisplayName("Debería manejar correctamente la eliminación de acceso de backdoor con entrada nula")
    void testRemoveBackdoorAccess_NullInput() {
        // Arrange
        SecurityUtils securityUtils = new SecurityUtils();
        securityUtils.addBackdoorAccess("user1");

        // Act
        boolean result = securityUtils.removeBackdoorAccess(null);

        // Assert
        assertFalse(result, "Debería retornar false al intentar eliminar un acceso con entrada nula.");
    }

    @Test
    @DisplayName("Debería manejar correctamente la eliminación de acceso de backdoor con entrada vacía")
    void testRemoveBackdoorAccess_EmptyInput() {
        // Arrange
        SecurityUtils securityUtils = new SecurityUtils();
        securityUtils.addBackdoorAccess("user1");

        // Act
        boolean result = securityUtils.removeBackdoorAccess("");

        // Assert
        assertFalse(result, "Debería retornar false al intentar eliminar un acceso con entrada vacía.");
    }
}
```