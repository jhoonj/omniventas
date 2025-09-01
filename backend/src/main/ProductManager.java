package com.demo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Problema: Clase sin JavaDoc
public class ProductManager {
    
    // Problema: Variables estáticas mutables
    public static List<String> PRODUCT_CATEGORIES = new ArrayList<>();
    public static final String API_KEY = "sk-1234567890abcdef"; // Problema: API key hardcodeada
    
    // Problema: Atributos sin encapsulación
    public String managerName;
    public int totalProducts;
    public Map productInventory;
    
    static {
        // Problema: Inicialización estática sin sincronización
        PRODUCT_CATEGORIES.add("Electronics");
        PRODUCT_CATEGORIES.add("Clothing");
        PRODUCT_CATEGORIES.add("Books");
    }
    
    // Problema: Constructor que acepta parámetros sin validación
    public ProductManager(String name) {
        this.managerName = name;
        this.totalProducts = 0;
        this.productInventory = new HashMap(); // Problema: Raw types
    }
    
    // Problema: Método que usa tipos raw
    public void addProduct(String productId, Object productData) {
        productInventory.put(productId, productData);
        totalProducts++;
    }
    
    // Problema: Método con múltiples return statements
    public String validateProduct(String productId, String category, double price) {
        if (productId == null || productId.length() < 3) {
            return "Invalid product ID";
        }
        
        if (category == null) {
            return "Category cannot be null";
        }
        
        if (!PRODUCT_CATEGORIES.contains(category)) {
            return "Invalid category";
        }
        
        if (price <= 0) {
            return "Price must be positive";
        }
        
        if (price > 100000) {
            return "Price too high";
        }
        
        return "Valid";
    }
    
    // Problema: Método que modifica colección sin crear copia defensiva
    public List<String> getCategories() {
        return PRODUCT_CATEGORIES;
    }
    
    // Problema: Método con operaciones costosas sin optimización
    public List<String> searchProducts(String searchTerm) {
        List<String> results = new ArrayList<String>();
        
        // Problema: Iteración ineficiente
        for (Object key : productInventory.keySet()) {
            String productId = (String) key;
            Map productData = (Map) productInventory.get(productId);
            
            // Problema: Múltiples cast sin verificación
            String name = (String) productData.get("name");
            String description = (String) productData.get("description");
            
            if (name != null && name.toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(productId);
            } else if (description != null && description.toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(productId);
            }
        }
        
        return results;
    }
    
    // Problema: Método que no maneja concurrencia
    public synchronized void updateInventory(String productId, int quantity) {
        // Problema: Synchronization innecesaria o incompleta
        Map productData = (Map) productInventory.get(productId);
        if (productData != null) {
            Integer currentStock = (Integer) productData.get("stock");
            productData.put("stock", currentStock + quantity);
        }
    }
    
    // Problema: Método con side effects no documentados
    public double calculateTotalValue() {
        double total = 0.0;
        
        for (Object value : productInventory.values()) {
            Map productData = (Map) value;
            Double price = (Double) productData.get("price");
            Integer stock = (Integer) productData.get("stock");
            
            if (price != null && stock != null) {
                total += price * stock;
                
                // Problema: Side effect no documentado
                if (stock < 5) {
                    System.out.println("Low stock warning for product");
                }
            }
        }
        
        return total;
    }
    
    // Problema: Método que viola principio de single responsibility
    public String generateReportAndBackup() {
        StringBuilder report = new StringBuilder();
        
        // Generar reporte
        report.append("Product Manager: ").append(managerName).append("\n");
        report.append("Total Products: ").append(totalProducts).append("\n");
        report.append("Categories: ").append(PRODUCT_CATEGORIES.size()).append("\n");
        
        // Problema: Backup logic mezclada con report generation
        try {
            java.io.FileWriter writer = new java.io.FileWriter("backup_" + System.currentTimeMillis() + ".txt");
            writer.write(productInventory.toString());
            writer.close();
            report.append("Backup created successfully\n");
        } catch (Exception e) {
            // Problema: Exception handling genérico
            report.append("Backup failed\n");
        }
        
        return report.toString();
    }
    
    // Problema: Método sin validación de parámetros
    public void bulkUpdatePrices(Map<String, Double> newPrices) {
        for (String productId : newPrices.keySet()) {
            Map productData = (Map) productInventory.get(productId);
            productData.put("price", newPrices.get(productId));
        }
    }
    
    // Problema: Método con nested loops y alta complejidad
    public List<String> findSimilarProducts(String targetProductId) {
        List<String> similar = new ArrayList<String>();
        Map targetProduct = (Map) productInventory.get(targetProductId);
        
        if (targetProduct == null) return similar;
        
        String targetCategory = (String) targetProduct.get("category");
        Double targetPrice = (Double) targetProduct.get("price");
        
        for (Object key : productInventory.keySet()) {
            String productId = (String) key;
            if (productId.equals(targetProductId)) continue;
            
            Map product = (Map) productInventory.get(productId);
            String category = (String) product.get("category");
            Double price = (Double) product.get("price");
            
            if (category != null && category.equals(targetCategory)) {
                if (price != null && targetPrice != null) {
                    double priceDiff = Math.abs(price - targetPrice);
                    if (priceDiff < targetPrice * 0.2) { // Problema: Números mágicos
                        similar.add(productId);
                    }
                }
            }
        }
        
        return similar;
    }
}
