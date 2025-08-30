package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// ❌ SERVICIO CON PROBLEMAS DE RENDIMIENTO Y ARQUITECTURA ❌

@Service
public class TerriblePerformanceService {
    
    @Autowired
    private Connection dbConnection;
    
    // ❌ Método con complejidad O(n³) innecesaria
    public List<String> processingUserData(List<Integer> userIds) {
        List<String> results = new ArrayList<>();
        
        for (int i = 0; i < userIds.size(); i++) {
            for (int j = 0; j < userIds.size(); j++) {
                for (int k = 0; k < userIds.size(); k++) {
                    // ❌ Query en loop anidado - N+1 problem extremo
                    try {
                        String sql = "SELECT username FROM users WHERE id = " + userIds.get(i);
                        Statement stmt = dbConnection.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);
                        
                        if (rs.next()) {
                            String username = rs.getString("username");
                            
                            // ❌ Procesamiento innecesario
                            for (int x = 0; x < 1000; x++) {
                                username = username.toUpperCase().toLowerCase();
                            }
                            
                            results.add(username + "_" + i + "_" + j + "_" + k);
                        }
                        
                        // ❌ No cerrar recursos
                        // stmt.close(); - INTENCIONALMENTE comentado
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return results;
    }
    
    // ❌ Método que carga TODO en memoria
    public void loadAllDataIntoMemory() {
        List<Map<String, Object>> allData = new ArrayList<>();
        
        try {
            // ❌ SELECT * sin LIMIT
            String sql = "SELECT * FROM users, orders, products, transactions, logs";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            // ❌ Cargar millones de registros en memoria
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                
                allData.add(row);
            }
            
            // ❌ Procesar datos en memoria de forma ineficiente
            for (Map<String, Object> row : allData) {
                for (Map<String, Object> otherRow : allData) {
                    // ❌ Comparación O(n²) innecesaria
                    if (row.get("id").equals(otherRow.get("id"))) {
                        System.out.println("Found match: " + row.get("id"));
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ❌ Método con múltiples llamadas HTTP síncronas
    public String synchronousHttpCalls() {
        StringBuilder result = new StringBuilder();
        
        // ❌ Múltiples llamadas HTTP bloqueantes
        String[] urls = {
            "http://api1.example.com/data",
            "http://api2.example.com/data", 
            "http://api3.example.com/data",
            "http://api4.example.com/data",
            "http://api5.example.com/data"
        };
        
        for (String url : urls) {
            try {
                // ❌ Sin timeout, sin manejo de errores adecuado
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                
                // ❌ No cerrar conexiones
                // reader.close();
                // connection.disconnect();
                
            } catch (Exception e) {
                result.append("Error calling " + url + ": " + e.getMessage());
            }
        }
        
        return result.toString();
    }
    
    // ❌ Método que no usa cache y recalcula todo siempre
    public double calculateExpensiveOperation(int input) {
        double result = 0;
        
        // ❌ Cálculo costoso que debería estar en cache
        for (int i = 0; i < 1000000; i++) {
            result += Math.pow(input, 2) + Math.sqrt(i) + Math.sin(i) + Math.cos(i);
            
            // ❌ Query en cada iteración
            try {
                String sql = "SELECT COUNT(*) FROM users WHERE created_date > NOW() - INTERVAL " + i + " DAY";
                Statement stmt = dbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                
                if (rs.next()) {
                    result += rs.getInt(1);
                }
                
                // ❌ No cerrar Statement
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
    
    // ❌ Método con memory leak intencionado
    private static List<byte[]> memoryLeakList = new ArrayList<>();
    
    public void createMemoryLeak() {
        // ❌ Añadir grandes objetos a lista estática sin limpiar
        for (int i = 0; i < 1000; i++) {
            byte[] bigArray = new byte[1024 * 1024]; // 1MB cada uno
            memoryLeakList.add(bigArray);
        }
        
        // ❌ Crear hilos que nunca terminan
        Thread leakyThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    // ❌ Más objects en memoria
                    List<String> moreLeaks = new ArrayList<>();
                    for (int i = 0; i < 10000; i++) {
                        moreLeaks.add("Leak " + i + " " + new Date());
                    }
                } catch (InterruptedException e) {
                    // ❌ No manejar interrupción correctamente
                    e.printStackTrace();
                }
            }
        });
        
        leakyThread.start(); // ❌ Nunca se detiene
    }
    
    // ❌ Método con transacciones mal manejadas
    public void badTransactionHandling() {
        try {
            dbConnection.setAutoCommit(false);
            
            // ❌ Múltiples operaciones sin consistencia
            Statement stmt = dbConnection.createStatement();
            
            stmt.executeUpdate("UPDATE accounts SET balance = 0"); // ❌ Peligroso
            stmt.executeUpdate("DELETE FROM user_sessions"); // ❌ Sin WHERE
            stmt.executeUpdate("INSERT INTO audit_log VALUES (1, 'SYSTEM RESET', NOW())");
            
            // ❌ Commit manual que puede fallar
            dbConnection.commit();
            
        } catch (Exception e) {
            // ❌ No hacer rollback en caso de error
            System.out.println("Transaction failed: " + e.getMessage());
            // dbConnection.rollback(); - INTENCIONALMENTE comentado
        }
    }
}
