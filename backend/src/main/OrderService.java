package com.demo.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    
    // Problema: Campos públicos en lugar de private con getters/setters
    public static String DEFAULT_STATUS = "PENDING";
    public List<String> validStatuses;
    
    // Problema: Constructor sin validación
    public OrderService() {
        validStatuses = new ArrayList<>();
        validStatuses.add("PENDING");
        validStatuses.add("CONFIRMED");
        validStatuses.add("SHIPPED");
        validStatuses.add("DELIVERED");
    }
    
    // Problema: Método sin JavaDoc y con lógica compleja
    public Map<String, Object> createOrder(String customerId, List<String> products, double totalAmount) {
        Map<String, Object> order = new HashMap<>();
        
        // Problema: Múltiples responsabilidades en un método
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new RuntimeException("Customer ID cannot be null or empty");
        }
        
        if (products == null || products.size() == 0) {
            throw new RuntimeException("Products list cannot be empty");
        }
        
        // Problema: Números mágicos
        if (totalAmount < 0 || totalAmount > 999999) {
            throw new RuntimeException("Invalid amount");
        }
        
        order.put("id", System.currentTimeMillis());
        order.put("customerId", customerId);
        order.put("products", products);
        order.put("totalAmount", totalAmount);
        order.put("status", DEFAULT_STATUS);
        order.put("createdDate", new Date());
        
        return order;
    }
    
    // Problema: Método que modifica estado sin sincronización
    public boolean updateOrderStatus(Map<String, Object> order, String newStatus) {
        if (order == null) return false;
        
        // Problema: Validación insuficiente
        if (!validStatuses.contains(newStatus)) {
            return false;
        }
        
        String currentStatus = (String) order.get("status");
        
        // Problema: Lógica de negocio hardcodeada
        if ("DELIVERED".equals(currentStatus)) {
            return false; // No se puede cambiar status de delivered
        }
        
        order.put("status", newStatus);
        order.put("updatedDate", new Date());
        
        return true;
    }
    
    // Problema: Método con nombre poco descriptivo
    public double calc(List<Map<String, Object>> orders) {
        double total = 0;
        
        // Problema: No maneja casos edge
        for (Map<String, Object> order : orders) {
            Object amount = order.get("totalAmount");
            if (amount instanceof Double) {
                total += (Double) amount;
            } else if (amount instanceof Integer) {
                total += ((Integer) amount).doubleValue();
            }
            // Problema: No maneja otros tipos
        }
        
        return total;
    }
    
    // Problema: Método que viola principio de responsabilidad única
    public String generateInvoiceAndSendEmail(Map<String, Object> order) {
        StringBuilder invoice = new StringBuilder();
        
        // Generar invoice
        invoice.append("INVOICE\n");
        invoice.append("Order ID: ").append(order.get("id")).append("\n");
        invoice.append("Customer: ").append(order.get("customerId")).append("\n");
        invoice.append("Amount: $").append(order.get("totalAmount")).append("\n");
        
        // Problema: Lógica de email mezclada con generación de invoice
        String customerEmail = getCustomerEmail((String) order.get("customerId"));
        if (customerEmail != null) {
            sendEmail(customerEmail, "Invoice", invoice.toString());
        }
        
        return invoice.toString();
    }
    
    // Problema: Método que simula envío de email sin implementación real
    private void sendEmail(String email, String subject, String body) {
        // TODO: Implementar envío real de email
        System.out.println("Sending email to: " + email);
    }
    
    // Problema: Método con lógica hardcodeada
    private String getCustomerEmail(String customerId) {
        // Problema: Datos hardcodeados en lugar de usar base de datos
        Map<String, String> customers = new HashMap<>();
        customers.put("CUST001", "john@email.com");
        customers.put("CUST002", "jane@email.com");
        
        return customers.get(customerId);
    }
    
    // Problema: Método sin manejo de excepciones
    public List<Map<String, Object>> getOrdersByStatus(List<Map<String, Object>> allOrders, String status) {
        List<Map<String, Object>> filteredOrders = new ArrayList<>();
        
        // Problema: No valida parámetros de entrada
        for (Map<String, Object> order : allOrders) {
            String orderStatus = (String) order.get("status");
            if (orderStatus.equals(status)) {
                filteredOrders.add(order);
            }
        }
        
        return filteredOrders;
    }
    
    // Problema: Método con complejidad ciclomática alta
    public String getOrderPriority(Map<String, Object> order) {
        Double amount = (Double) order.get("totalAmount");
        String customerId = (String) order.get("customerId");
        Date createdDate = (Date) order.get("createdDate");
        
        // Problema: Múltiples condiciones anidadas
        if (amount > 10000) {
            if (customerId.startsWith("VIP")) {
                return "URGENT";
            } else if (customerId.startsWith("PREMIUM")) {
                return "HIGH";
            } else {
                if (new Date().getTime() - createdDate.getTime() > 86400000) { // 24 horas
                    return "HIGH";
                } else {
                    return "MEDIUM";
                }
            }
        } else if (amount > 1000) {
            if (customerId.startsWith("VIP") || customerId.startsWith("PREMIUM")) {
                return "MEDIUM";
            } else {
                return "LOW";
            }
        } else {
            return "LOW";
        }
    }
    
    // Problema: Método que no valida entrada y puede lanzar NullPointerException
    public boolean isValidOrder(Map<String, Object> order) {
        return order.get("id") != null && 
               order.get("customerId") != null &&
               order.get("products") != null &&
               ((List<?>) order.get("products")).size() > 0 &&
               order.get("totalAmount") != null;
    }
}
