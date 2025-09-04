```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private SomeDependency someDependency; // Reemplazar con la dependencia real si es necesario

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería procesar datos de usuario exitosamente")
    void testProcessUserData_Success() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2");
        when(someDependency.process(usernames)).thenReturn(Optional.of("Processed Data"));

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed Data", result.get());
        verify(someDependency, times(1)).process(usernames);
    }

    @Test
    @DisplayName("Debería retornar vacío cuando no se proporcionan nombres de usuario")
    void testProcessUserData_EmptyUsernames() {
        // Arrange
        List<String> usernames = Collections.emptyList();

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
        verify(someDependency, never()).process(any());
    }

    @Test
    @DisplayName("Debería manejar excepción al procesar datos de usuario")
    void testProcessUserData_Exception() {
        // Arrange
        List<String> usernames = Arrays.asList("user1");
        when(someDependency.process(usernames)).thenThrow(new RuntimeException("Error processing"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.processUserData(usernames);
        });

        assertEquals("Error processing", exception.getMessage());
        verify(someDependency, times(1)).process(usernames);
    }

    @Test
    @DisplayName("Debería retornar vacío cuando se procesan nombres de usuario inválidos")
    void testProcessUserData_InvalidUsernames() {
        // Arrange
        List<String> usernames = Arrays.asList("invalid_user");
        when(someDependency.process(usernames)).thenReturn(Optional.empty());

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
        verify(someDependency, times(1)).process(usernames);
    }
}
```