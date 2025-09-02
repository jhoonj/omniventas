```java
package backend.src.main.demo-code;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository; // Suponiendo que OrderService depende de OrderRepository

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create order successfully")
    public void testCreateOrder_Success() {
        // Arrange
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("customerId", 1);
        orderData.put("productId", 2);
        orderData.put("quantity", 3);

        when(orderRepository.save(any(Order.class))).thenReturn(new Order(1, 1, 2, 3));

        // Act
        Map<String, Object> result = orderService.createOrder(orderData);

        // Assert
        assertNotNull(result);
        assertEquals("Order created successfully", result.get("message"));
        assertEquals(1, result.get("orderId"));
    }

    @Test
    @DisplayName("Should throw exception when customerId is missing")
    public void testCreateOrder_MissingCustomerId() {
        // Arrange
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("productId", 2);
        orderData.put("quantity", 3);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderData);
        });

        assertEquals("Customer ID is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when productId is missing")
    public void testCreateOrder_MissingProductId() {
        // Arrange
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("customerId", 1);
        orderData.put("quantity", 3);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderData);
        });

        assertEquals("Product ID is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when quantity is less than 1")
    public void testCreateOrder_InvalidQuantity() {
        // Arrange
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("customerId", 1);
        orderData.put("productId", 2);
        orderData.put("quantity", 0);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderData);
        });

        assertEquals("Quantity must be at least 1", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle repository error gracefully")
    public void testCreateOrder_RepositoryError() {
        // Arrange
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("customerId", 1);
        orderData.put("productId", 2);
        orderData.put("quantity", 3);

        when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderData);
        });

        assertEquals("Database error", exception.getMessage());
    }
}
```