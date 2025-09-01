```java
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
    private SomeRepository someRepository; // Suponiendo que hay un repositorio

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería ejecutar la lógica de negocio exitosamente")
    void testBusinessLogicSuccess() {
        // Arrange
        SomeEntity entity = new SomeEntity(); // Suponiendo que hay una entidad
        when(someRepository.save(entity)).thenReturn(entity);

        // Act
        terriblePerformanceService.executeBusinessLogic(entity);

        // Assert
        verify(someRepository).save(entity);
    }

    @Test
    @DisplayName("Debería manejar excepción y realizar rollback")
    void testBusinessLogicExceptionHandling() {
        // Arrange
        SomeEntity entity = new SomeEntity(); // Suponiendo que hay una entidad
        doThrow(new RuntimeException("Error al guardar")).when(someRepository).save(entity);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            terriblePerformanceService.executeBusinessLogic(entity);
        });

        // Assert
        verify(someRepository).save(entity);
        // Aquí se podría verificar el rollback si hay un método para ello
    }

    @Test
    @DisplayName("Debería manejar caso de entidad nula")
    void testBusinessLogicWithNullEntity() {
        // Arrange
        SomeEntity entity = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            terriblePerformanceService.executeBusinessLogic(entity);
        });
    }

    @Test
    @DisplayName("Debería manejar caso de entidad vacía")
    void testBusinessLogicWithEmptyEntity() {
        // Arrange
        SomeEntity entity = new SomeEntity(); // Suponiendo que hay una entidad
        entity.setField(""); // Suponiendo que hay un campo que no puede estar vacío

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            terriblePerformanceService.executeBusinessLogic(entity);
        });
    }
}
```