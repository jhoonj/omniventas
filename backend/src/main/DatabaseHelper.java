import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Problema: Sin package declaration
// Problema: Imports no organizados

public class DatabaseHelper {
    
    // Problema: Credenciales hardcodeadas
    private static final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password123";
    
    private Connection connection;
    private static DatabaseHelper instance; // Problema: Singleton mal implementado
    
    // Problema: Constructor que puede lanzar excepciones no documentadas
    public DatabaseHelper() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
    
    // Problema: Singleton sin sincronización
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            try {
                instance = new DatabaseHelper();
            } catch (SQLException e) {
                e.printStackTrace(); // Problema: printStackTrace() en lugar de logging
            }
        }
        return instance;
    }
    
    // Problema: SQL injection vulnerable
    public List<Map<String, Object>> executeQuery(String query) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query); // Problema: No usa PreparedStatement
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
            
            // Problema: No cierra recursos
            
        } catch (SQLException e) {
            e.printStackTrace(); // Problema: No maneja errores apropiadamente
        }
        
        return results;
    }
    
    // Problema: Método que no valida parámetros
    public boolean insertUser(String name, String email, int age) {
        String sql = "INSERT INTO users (name, email, age) VALUES ('" + 
                     name + "', '" + email + "', " + age + ")"; // Problema: SQL injection
        
        try {
            Statement stmt = connection.createStatement();
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            return false; // Problema: Silencia errores
        }
    }
    
    // Problema: Método con lógica compleja y múltiples responsabilidades
    public String generateUserReport(String department) {
        StringBuilder report = new StringBuilder();
        
        // Problema: Query hardcodeada con concatenación
        String query = "SELECT * FROM users WHERE department = '" + department + "'";
        List<Map<String, Object>> users = executeQuery(query);
        
        report.append("DEPARTMENT REPORT: ").append(department).append("\n");
        report.append("Generated on: ").append(new Date()).append("\n");
        report.append("Total users: ").append(users.size()).append("\n\n");
        
        double totalSalary = 0;
        int activeUsers = 0;
        
        for (Map<String, Object> user : users) {
            String status = (String) user.get("status");
            Double salary = (Double) user.get("salary");
            
            if ("ACTIVE".equals(status)) {
                activeUsers++;
            }
            
            if (salary != null) {
                totalSalary += salary;
            }
            
            // Problema: Formateado de reporte mezclado con cálculos
            report.append("User: ").append(user.get("name"));
            report.append(" | Status: ").append(status);
            report.append(" | Salary: $").append(salary).append("\n");
        }
        
        report.append("\nSUMMARY:\n");
        report.append("Active users: ").append(activeUsers).append("\n");
        report.append("Total salary: $").append(totalSalary).append("\n");
        report.append("Average salary: $").append(users.size() > 0 ? totalSalary / users.size() : 0).append("\n");
        
        // Problema: Side effect - escribir archivo
        try {
            FileWriter writer = new FileWriter("report_" + department + ".txt");
            writer.write(report.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("Failed to save report"); // Problema: Manejo de errores inconsistente
        }
        
        return report.toString();
    }
    
    // Problema: Método que puede causar memory leak
    public void batchInsert(List<Map<String, String>> userData) {
        for (Map<String, String> data : userData) {
            // Problema: Crea statement en cada iteración
            try {
                Statement stmt = connection.createStatement();
                String sql = "INSERT INTO users (name, email, department) VALUES ('" +
                           data.get("name") + "', '" + data.get("email") + "', '" + 
                           data.get("department") + "')";
                stmt.executeUpdate(sql);
                // Problema: No cierra statement
            } catch (SQLException e) {
                continue; // Problema: Ignora errores silenciosamente
            }
        }
    }
    
    // Problema: Método sin sincronización para operación crítica
    public void updateUserSalary(int userId, double newSalary) {
        String query = "SELECT salary FROM users WHERE id = " + userId;
        List<Map<String, Object>> result = executeQuery(query);
        
        if (!result.isEmpty()) {
            Map<String, Object> user = result.get(0);
            Double currentSalary = (Double) user.get("salary");
            
            // Problema: Lógica de negocio hardcodeada
            if (newSalary > currentSalary * 1.5) {
                System.out.println("Warning: Salary increase > 50%");
            }
            
            String updateSql = "UPDATE users SET salary = " + newSalary + 
                             " WHERE id = " + userId;
            executeQuery(updateSql); // Problema: Usa executeQuery para UPDATE
        }
    }
    
    // Problema: Método que expone detalles internos
    public Connection getConnection() {
        return connection;
    }
    
    // Problema: Método close mal implementado
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // Problema: No hace nada con la excepción
        }
        // Problema: No resetea instance en singleton
    }
    
    // Problema: Método utilitario en clase de acceso a datos
    public static String formatCurrency(double amount) {
        return "$" + String.format("%.2f", amount);
    }
    
    // Problema: Método con demasiados parámetros
    public boolean createUserAccount(String firstName, String lastName, String email, 
                                   String phone, String address, String city, String state, 
                                   String zipCode, String department, String position, 
                                   double salary, Date startDate) {
        
        String sql = "INSERT INTO users (first_name, last_name, email, phone, address, " +
                    "city, state, zip_code, department, position, salary, start_date) VALUES ('" +
                    firstName + "', '" + lastName + "', '" + email + "', '" + phone + "', '" +
                    address + "', '" + city + "', '" + state + "', '" + zipCode + "', '" +
                    department + "', '" + position + "', " + salary + ", '" + startDate + "')";
        
        try {
            Statement stmt = connection.createStatement();
            int result = stmt.executeUpdate(sql);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }
}
