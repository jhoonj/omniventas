```java
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return user when user exists")
    void testGetUserById_UserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "John Doe", "john.doe@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("John Doe", result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void testGetUserById_UserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Should create a new user successfully")
    void testCreateUser_Success() {
        // Arrange
        User user = new User(null, "Jane Doe", "jane.doe@example.com");
        when(userRepository.save(any(User.class))).thenReturn(new User(1L, "Jane Doe", "jane.doe@example.com"));

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals("Jane Doe", createdUser.getName());
        assertEquals("jane.doe@example.com", createdUser.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void testCreateUser_EmailAlreadyExists() {
        // Arrange
        User user = new User(null, "Jane Doe", "jane.doe@example.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("Should update user successfully")
    void testUpdateUser_Success() {
        // Arrange
        Long userId = 1L;
        User existingUser = new User(userId, "John Doe", "john.doe@example.com");
        User updatedUser = new User(userId, "John Smith", "john.smith@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals("John Smith", result.getName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing user")
    void testUpdateUser_UserDoesNotExist() {
        // Arrange
        Long userId = 1L;
        User updatedUser = new User(userId, "John Smith", "john.smith@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updatedUser));
        verify(userRepository, times(1)).findById(userId);
    }
}
```