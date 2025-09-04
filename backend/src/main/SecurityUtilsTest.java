```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class SecurityUtilsTest {

    @Mock
    private Connection connection;

    @InjectMocks
    private SecurityUtils securityUtils;

    public SecurityUtilsTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return permissions for valid user and resource")
    void testGetPermissions_Success() throws SQLException {
        // Arrange
        String userId = "123";
        String resource = "resource1";
        String expectedPermissions = "READ,WRITE";

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
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
    @DisplayName("Should return null for user with no permissions")
    void testGetPermissions_NoPermissions() throws SQLException {
        // Arrange
        String userId = "123";
        String resource = "resource1";

        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
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

    @Test
    @DisplayName("Should throw SQLException when database error occurs")
    void testGetPermissions_DatabaseError() throws SQLException {
        // Arrange
        String userId = "123";
        String resource = "resource1";

        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // Act & Assert
        SQLException exception = assertThrows(SQLException.class, () -> {
            securityUtils.getPermissions(connection, userId, resource);
        });
        assertEquals("Database error", exception.getMessage());
        verify(preparedStatement).setString(1, userId);
        verify(preparedStatement).setString(2, resource);
        verify(preparedStatement).executeQuery();
    }
}
```