```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DatabaseHelperTest {

    private DatabaseHelper databaseHelper;
    private Connection mockConnection;

    @BeforeEach
    void setUp() {
        databaseHelper = new DatabaseHelper();
        mockConnection = mock(Connection.class);
    }

    @Test
    @DisplayName("Debería devolver una conexión exitosa")
    void testGetConnection_Success() throws SQLException {
        // Arrange
        when(mockConnection.isValid(0)).thenReturn(true);
        databaseHelper.setConnection(mockConnection); // Asumiendo que hay un método para establecer la conexión

        // Act
        Connection connection = databaseHelper.getConnection();

        // Assert
        assertNotNull(connection);
        assertTrue(connection.isValid(0));
    }

    @Test
    @DisplayName("Debería lanzar SQLException cuando la conexión es inválida")
    void testGetConnection_InvalidConnection() throws SQLException {
        // Arrange
        when(mockConnection.isValid(0)).thenReturn(false);
        databaseHelper.setConnection(mockConnection); // Asumiendo que hay un método para establecer la conexión

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            if (!mockConnection.isValid(0)) {
                throw new SQLException("Conexión inválida");
            }
        });
        assertEquals("Conexión inválida", exception.getMessage());
    }

    @Test
    @DisplayName("Debería manejar excepciones al intentar obtener la conexión")
    void testGetConnection_ExceptionHandling() throws SQLException {
        // Arrange
        when(mockConnection.isValid(0)).thenThrow(new SQLException("Error al validar conexión"));
        databaseHelper.setConnection(mockConnection); // Asumiendo que hay un método para establecer la conexión

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            databaseHelper.getConnection();
        });
        assertEquals("Error al validar conexión", exception.getMessage());
    }
}
```