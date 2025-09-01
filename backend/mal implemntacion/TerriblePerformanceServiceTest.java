```java
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("TerriblePerformanceService Test")
class TerriblePerformanceServiceTest {

    @InjectMocks
    private TerriblePerformanceService terriblePerformanceService;

    @Mock
    private SomeRepository someRepository; // Asume que hay un repositorio que se usa en el servicio

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería ejecutar la lógica de negocio exitosamente")
    void testExecuteBusinessLogicSuccess() {
        // Arrange
        // Configura el comportamiento del mock si es necesario
        when(someRepository.someMethod()).thenReturn(someExpectedValue);

        // Act
        var result = terriblePerformanceService.executeBusinessLogic();

        // Assert
        assertEquals(expectedValue, result);
    }

    @Test
    @DisplayName("Debería manejar la excepción y realizar rollback")
    void testExecuteBusinessLogicHandlesException() {
        // Arrange
        doThrow(new RuntimeException("Error de prueba")).when(someRepository).someMethod();

        // Act
        var result = terriblePerformanceService.executeBusinessLogic();

        // Assert
        assertEquals(expectedRollbackValue, result);
        // Aquí podrías verificar que el rollback se haya realizado correctamente
    }

    @Test
    @DisplayName("Debería manejar un caso límite")
    void testExecuteBusinessLogicEdgeCase() {
        // Arrange
        when(someRepository.someMethod()).thenReturn(edgeCaseValue);

        // Act
        var result = terriblePerformanceService.executeBusinessLogic();

        // Assert
        assertEquals(expectedEdgeCaseValue, result);
    }

    @Test
    @DisplayName("Debería lanzar excepción si la entrada es inválida")
    void testExecuteBusinessLogicInvalidInput() {
        // Arrange
        // Configura un caso de entrada inválida

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            terriblePerformanceService.executeBusinessLogic(invalidInput);
        });
    }
}
```