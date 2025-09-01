```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TerriblePerformanceServiceTest {

    @InjectMocks
    private TerriblePerformanceService terriblePerformanceService;

    @Mock
    private SomeRepository someRepository; // Suponiendo que hay un repositorio

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería ejecutar la lógica de negocio exitosamente")
    void testBusinessLogicSuccess() {
        // Arrange
        // Configurar el comportamiento esperado del repositorio

        // Act
        String result = terriblePerformanceService.executeBusinessLogic();

        // Assert
        assertEquals("Expected Result", result);
        verify(someRepository).someMethod(); // Verificar que se llamó al método del repositorio
    }

    @Test
    @DisplayName("Debería manejar la excepción correctamente")
    void testBusinessLogicExceptionHandling() {
        // Arrange
        doThrow(new RuntimeException("Error en el repositorio")).when(someRepository).someMethod();

        // Act
        String result = terriblePerformanceService.executeBusinessLogic();

        // Assert
        assertEquals("Fallback Result", result); // Verificar el resultado esperado en caso de error
        // Verificar que se realizó el rollback o cualquier otra acción necesaria
    }

    @Test
    @DisplayName("Debería manejar un caso límite")
    void testBusinessLogicEdgeCase() {
        // Arrange
        // Configurar el repositorio para un caso límite

        // Act
        String result = terriblePerformanceService.executeBusinessLogic();

        // Assert
        assertEquals("Edge Case Result", result); // Verificar el resultado esperado para el caso límite
    }
}
```