```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    private SomeDependency someDependency; // Suponiendo que hay una dependencia

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería ejecutar la lógica de negocio exitosamente")
    void testBusinessLogicSuccess() {
        // Arrange
        when(someDependency.someMethod()).thenReturn(someExpectedValue);

        // Act
        var result = terriblePerformanceService.someBusinessMethod();

        // Assert
        assertEquals(expectedValue, result);
        verify(someDependency, times(1)).someMethod();
    }

    @Test
    @DisplayName("Debería manejar excepción y realizar rollback")
    void testBusinessLogicExceptionHandling() {
        // Arrange
        when(someDependency.someMethod()).thenThrow(new RuntimeException("Error"));

        // Act
        var result = terriblePerformanceService.someBusinessMethod();

        // Assert
        assertNull(result);
        verify(someDependency, times(1)).someMethod();
        // Aquí podrías verificar que se realizó el rollback si hay un método para ello
    }

    @Test
    @DisplayName("Debería manejar caso límite correctamente")
    void testBusinessLogicEdgeCase() {
        // Arrange
        when(someDependency.someMethod()).thenReturn(edgeCaseValue);

        // Act
        var result = terriblePerformanceService.someBusinessMethod();

        // Assert
        assertEquals(expectedEdgeCaseValue, result);
        verify(someDependency, times(1)).someMethod();
    }

    @Test
    @DisplayName("Debería lanzar excepción para entradas inválidas")
    void testBusinessLogicInvalidInput() {
        // Arrange
        // Configurar el escenario para una entrada inválida

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            terriblePerformanceService.someBusinessMethod(invalidInput);
        });
    }
}
```