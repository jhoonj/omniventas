```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TerriblePerformanceServiceTest {

    @InjectMocks
    private TerriblePerformanceService terriblePerformanceService;

    @Mock
    private SomeRepository someRepository; // Reemplaza con el repositorio real

    public TerriblePerformanceServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería ejecutar la lógica de negocio correctamente")
    void testBusinessLogicSuccess() {
        // Arrange
        // Configura el comportamiento esperado del mock
        when(someRepository.someMethod()).thenReturn(expectedValue);

        // Act
        var result = terriblePerformanceService.someBusinessMethod();

        // Assert
        assertEquals(expectedValue, result);
    }

    @Test
    @DisplayName("Debería manejar la excepción correctamente")
    void testBusinessLogicExceptionHandling() {
        // Arrange
        doThrow(new RuntimeException("Error en la lógica")).when(someRepository).someMethod();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            terriblePerformanceService.someBusinessMethod();
        });
    }

    @Test
    @DisplayName("Debería realizar rollback en caso de error")
    void testRollbackOnError() {
        // Arrange
        doThrow(new RuntimeException("Error en la lógica")).when(someRepository).someMethod();

        // Act
        try {
            terriblePerformanceService.someBusinessMethod();
        } catch (RuntimeException e) {
            // Ignorar la excepción
        }

        // Assert
        // Verifica que el rollback se haya ejecutado correctamente
        // Esto puede requerir un método en el repositorio para verificar el estado
        // assertTrue(someRepository.isRollbackExecuted());
    }

    @Test
    @DisplayName("Debería manejar casos límite correctamente")
    void testEdgeCaseHandling() {
        // Arrange
        when(someRepository.someMethod()).thenReturn(edgeCaseValue);

        // Act
        var result = terriblePerformanceService.someBusinessMethod();

        // Assert
        assertEquals(edgeCaseValue, result);
    }
}
```