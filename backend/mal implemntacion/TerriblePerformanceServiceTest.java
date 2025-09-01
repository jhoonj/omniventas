```java
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TerriblePerformanceServiceTest {

    @Mock
    private SomeRepository someRepository; // Reemplaza con el repositorio real

    @InjectMocks
    private TerriblePerformanceService terriblePerformanceService;

    public TerriblePerformanceServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería ejecutar la lógica de negocio exitosamente")
    void testBusinessLogicSuccess() {
        // Arrange
        // Configura el comportamiento esperado del repositorio si es necesario

        // Act
        terriblePerformanceService.executeBusinessLogic();

        // Assert
        // Verifica que el repositorio haya sido llamado correctamente
        verify(someRepository).someMethod(); // Reemplaza con el método real
    }

    @Test
    @DisplayName("Debería manejar la excepción y realizar rollback")
    void testBusinessLogicExceptionHandling() {
        // Arrange
        doThrow(new RuntimeException("Error en la lógica de negocio"))
                .when(someRepository).someMethod(); // Reemplaza con el método real

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            terriblePerformanceService.executeBusinessLogic();
        });

        // Assert
        // Verifica que el rollback se haya realizado
        verify(someRepository).rollback(); // Reemplaza con el método real
    }

    @Test
    @DisplayName("Debería manejar edge case correctamente")
    void testBusinessLogicEdgeCase() {
        // Arrange
        // Configura el comportamiento del repositorio para un caso límite

        // Act
        terriblePerformanceService.executeBusinessLogic();

        // Assert
        // Verifica el comportamiento esperado para el caso límite
        verify(someRepository).someEdgeCaseMethod(); // Reemplaza con el método real
    }
}
```