```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private SomeDependency someDependency; // Reemplaza con las dependencias reales

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Debería procesar correctamente los datos de usuario y devolver un resultado")
    void testProcessUserData_Success() {
        // Arrange
        List<String> usernames = Arrays.asList("user1", "user2");
        when(someDependency.someMethod(anyList())).thenReturn("Processed Data");

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Processed Data", result.get());
        verify(someDependency).someMethod(usernames);
    }

    @Test
    @DisplayName("Debería devolver un Optional vacío cuando la lista de usuarios está vacía")
    void testProcessUserData_EmptyList() {
        // Arrange
        List<String> usernames = Collections.emptyList();

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Debería manejar una excepción y devolver un Optional vacío en caso de error")
    void testProcessUserData_Error() {
        // Arrange
        List<String> usernames = Arrays.asList("user1");
        when(someDependency.someMethod(anyList())).thenThrow(new RuntimeException("Error processing"));

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
        verify(someDependency).someMethod(usernames);
    }

    @Test
    @DisplayName("Debería manejar un caso de usuario nulo y devolver un Optional vacío")
    void testProcessUserData_NullUsernames() {
        // Arrange
        List<String> usernames = null;

        // Act
        Optional<String> result = userService.processUserData(usernames);

        // Assert
        assertFalse(result.isPresent());
    }
}
```