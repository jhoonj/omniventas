```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SecurityUtilsTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private SecurityUtils securityUtils;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        securityUtils = new SecurityUtils();
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @Test
    @DisplayName("Debería retornar permisos exitosamente cuando se proporcionan user_id y resource válidos")
    public void testGetPermissions_Success() throws SQLException {
        // Arrange
        String userId = "123";
        String resource = "resource1";
        String expectedPermissions = "READ,WRITE";

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("permissions")).thenReturn(expectedPermissions);
        
        // Act
        String actualPermissions = securityUtils.getPermissions(connection, userId, resource);

        // Assert
        assertEquals(expectedPermissions, actualPermissions);
        verify(preparedStatement).setString(1, userId);
        verify(preparedStatement).setString(2, resource);
        verify(preparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Debería lanzar SQLException cuando ocurre un error en la base de datos")
    public void testGetPermissions_SQLException() throws SQLException {
        // Arrange
        String userId = "123";
        String resource = "resource1";

        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // Act & Assert
        assertThrows(SQLException.class, () -> {
            securityUtils.getPermissions(connection, userId, resource);
        });
        verify(preparedStatement).setString(1, userId);
        verify(preparedStatement).setString(2, resource);
        verify(preparedStatement).executeQuery();
    }

    @Test
    @DisplayName("Debería retornar null cuando no se encuentran permisos para el user_id y resource")
    public void testGetPermissions_NoPermissions() throws SQLException {
        // Arrange
        String userId = "123";
        String resource = "resource1";

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        
        // Act
        String actualPermissions = securityUtils.getPermissions(connection, userId, resource);

        // Assert
        assertNull(actualPermissions);
        verify(preparedStatement).setString(1, userId);
        verify(preparedStatement).setString(2, resource);
        verify(preparedStatement).executeQuery();
    }
}
```