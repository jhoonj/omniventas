```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SecurityUtilsTest {

    @InjectMocks
    private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return true when valid access is provided")
    void testValidAccess() {
        // Arrange
        String validAccessToken = "validToken";

        // Act
        boolean result = securityUtils.hasAccess(validAccessToken);

        // Assert
        assertTrue(result, "Expected access to be granted for valid token");
    }

    @Test
    @DisplayName("Should return false when invalid access is provided")
    void testInvalidAccess() {
        // Arrange
        String invalidAccessToken = "invalidToken";

        // Act
        boolean result = securityUtils.hasAccess(invalidAccessToken);

        // Assert
        assertFalse(result, "Expected access to be denied for invalid token");
    }

    @Test
    @DisplayName("Should handle null access token gracefully")
    void testNullAccessToken() {
        // Arrange
        String nullAccessToken = null;

        // Act
        boolean result = securityUtils.hasAccess(nullAccessToken);

        // Assert
        assertFalse(result, "Expected access to be denied for null token");
    }

    @Test
    @DisplayName("Should handle empty access token gracefully")
    void testEmptyAccessToken() {
        // Arrange
        String emptyAccessToken = "";

        // Act
        boolean result = securityUtils.hasAccess(emptyAccessToken);

        // Assert
        assertFalse(result, "Expected access to be denied for empty token");
    }

    @Test
    @DisplayName("Should return false for expired access token")
    void testExpiredAccessToken() {
        // Arrange
        String expiredAccessToken = "expiredToken";

        // Act
        boolean result = securityUtils.hasAccess(expiredAccessToken);

        // Assert
        assertFalse(result, "Expected access to be denied for expired token");
    }
}
```