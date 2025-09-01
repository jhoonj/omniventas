```java
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    private SomeRepository someRepository; // Suponiendo que hay un repositorio que se usa

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería ejecutar la lógica de negocio exitosamente")
    void testBusinessLogicSuccess() {
        // Arrange
        // Configurar el comportamiento esperado del repositorio
        when(someRepository.someMethod()).thenReturn(someExpectedValue);

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
    void testBusinessLogicRollbackOnError() {
        // Arrange
        doThrow(new RuntimeException("Error en la lógica")).when(someRepository).someMethod();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            terriblePerformanceService.someBusinessMethod();
        });

        // Aquí podrías verificar que el rollback se haya ejecutado correctamente
        // Esto depende de cómo esté implementado el rollback en tu servicio
    }

    @Test
    @DisplayName("Debería manejar casos límite")
    void testBusinessLogicEdgeCase() {
        // Arrange
        when(someRepository.someMethod()).thenReturn(edgeCaseValue);

        // Act
        var result = terriblePerformanceService.someBusinessMethod();

        // Assert
        assertEquals(expectedEdgeCaseValue, result);
    }
}
```