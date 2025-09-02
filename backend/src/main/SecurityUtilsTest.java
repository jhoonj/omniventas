```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

@DisplayName("Tests for SecurityUtils")
class SecurityUtilsTest {

    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        securityUtils = new SecurityUtils();
    }

    @Test
    @DisplayName("Should return true for valid access")
    void testValidAccess() {
        // Arrange
        String userRole = "ADMIN";
        String requiredRole = "ADMIN";

        // Act
        boolean hasAccess = securityUtils.hasAccess(userRole, requiredRole);

        // Assert
        assertTrue(hasAccess, "User should have access with valid role");
    }

    @Test
    @DisplayName("Should return false for invalid access")
    void testInvalidAccess() {
        // Arrange
        String userRole = "USER";
        String requiredRole = "ADMIN";

        // Act
        boolean hasAccess = securityUtils.hasAccess(userRole, requiredRole);

        // Assert
        assertFalse(hasAccess, "User should not have access with invalid role");
    }

    @Test
    @DisplayName("Should handle null user role gracefully")
    void testNullUserRole() {
        // Arrange
        String userRole = null;
        String requiredRole = "ADMIN";

        // Act
        boolean hasAccess = securityUtils.hasAccess(userRole, requiredRole);

        // Assert
        assertFalse(hasAccess, "Null user role should not grant access");
    }

    @Test
    @DisplayName("Should handle null required role gracefully")
    void testNullRequiredRole() {
        // Arrange
        String userRole = "ADMIN";
        String requiredRole = null;

        // Act
        boolean hasAccess = securityUtils.hasAccess(userRole, requiredRole);

        // Assert
        assertFalse(hasAccess, "Null required role should not grant access");
    }

    @Test
    @DisplayName("Should return false for empty user role")
    void testEmptyUserRole() {
        // Arrange
        String userRole = "";
        String requiredRole = "ADMIN";

        // Act
        boolean hasAccess = securityUtils.hasAccess(userRole, requiredRole);

        // Assert
        assertFalse(hasAccess, "Empty user role should not grant access");
    }

    @Test
    @DisplayName("Should return false for empty required role")
    void testEmptyRequiredRole() {
        // Arrange
        String userRole = "ADMIN";
        String requiredRole = "";

        // Act
        boolean hasAccess = securityUtils.hasAccess(userRole, requiredRole);

        // Assert
        assertFalse(hasAccess, "Empty required role should not grant access");
    }
}
```