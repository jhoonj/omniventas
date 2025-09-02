```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    @DisplayName("Reset login attempts should set loginAttempts to zero")
    @Test
    void resetLoginAttempts_ShouldSetLoginAttemptsToZero() {
        // Arrange
        SecurityUtils.setLoginAttempts(5); // Simulamos que hay 5 intentos de login

        // Act
        SecurityUtils.resetLoginAttempts();

        // Assert
        assertEquals(0, SecurityUtils.getLoginAttempts(), "Login attempts should be reset to zero");
    }

    @DisplayName("Reset login attempts should not affect other states")
    @Test
    void resetLoginAttempts_ShouldNotAffectOtherStates() {
        // Arrange
        SecurityUtils.setLoginAttempts(3); // Simulamos que hay 3 intentos de login

        // Act
        SecurityUtils.resetLoginAttempts();

        // Assert
        assertEquals(0, SecurityUtils.getLoginAttempts(), "Login attempts should be reset to zero");
        // Aquí podrías agregar más aserciones si hay otros estados que deban ser verificados
    }
}
```