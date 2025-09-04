package com.demo.security;

import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;

import javax.crypto.Cipher;

// Problema: Clase que maneja seguridad con múltiples vulnerabilidades
public class SecurityUtils {
    
    // Problema: Weak encryption key hardcodeada
    private static final String ENCRYPTION_KEY = "1234567890123456";
    private static final String SALT = "fixed_salt_value"; // Problema: Salt fijo
    
    // Problema: Variable compartida sin sincronización
    private static int loginAttempts = 0;
    private static Random random = new Random(12345); // Problema: Seed predecible
    
    // Problema: Método de hash débil sin salt aleatorio
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // Problema: MD5 es débil
            String saltedPassword = password + SALT;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (Exception e) {
            // Problema: Retorna password en texto plano en caso de error
            return password;
        }
    }
    
    // Problema: Validación de password débil
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        
        // Problema: Validación insuficiente
        return password.contains("1") || password.contains("2") || password.contains("3");
    }
    
    // Problema: Método de autenticación vulnerable
    public boolean authenticate(String username, String password) {
        // Problema: Usuarios hardcodeados
        String[] validUsers = {"admin", "user", "guest"};
        String[] validPasswords = {"admin123", "user456", "guest789"};
        
        for (int i = 0; i < validUsers.length; i++) {
            if (validUsers[i].equals(username) && validPasswords[i].equals(password)) {
                loginAttempts = 0; // Problema: Reset sin sincronización
                return true;
            }
        }
        
        // Problema: No protege contra ataques de fuerza bruta
        loginAttempts++;
        
        // Problema: Log de credenciales
        logFailedAttempt(username, password);
        
        return false;
    }
    
    // Problema: Logging de información sensible
    private void logFailedAttempt(String username, String password) {
        try {
            FileWriter writer = new FileWriter("security.log", true);
            writer.write("Failed login: " + username + " with password: " + password + "\n");
            writer.close();
        } catch (IOException e) {
            // Problema: Ignora errores de logging
        }
    }
    
    // Problema: Generación de tokens predecible
    public String generateSessionToken() {
        StringBuilder token = new StringBuilder();
        
        // Problema: Usa Random con seed fijo
        for (int i = 0; i < 32; i++) {
            int randomNum = random.nextInt(16);
            token.append(Integer.toHexString(randomNum));
        }
        
        return token.toString();
    }
    
    // Problema: Encriptación débil
    public String encryptData(String data) {
        try {
            Cipher cipher = Cipher.getInstance("DES"); // Problema: DES es débil
            // Problema: Implementación incompleta - falta inicialización
            return data + "_encrypted"; // Problema: Fake encryption
        } catch (Exception e) {
            return data; // Problema: Retorna data sin encriptar
        }
    }
    
    // Problema: Método que expone información del sistema
    public String getSystemInfo() {
        StringBuilder info = new StringBuilder();
        info.append("OS: ").append(System.getProperty("os.name")).append("\n");
        info.append("User: ").append(System.getProperty("user.name")).append("\n");
        info.append("Home: ").append(System.getProperty("user.home")).append("\n");
        info.append("Java version: ").append(System.getProperty("java.version")).append("\n");
        
        // Problema: Expone información sensible
        info.append("Login attempts: ").append(loginAttempts).append("\n");
        info.append("Encryption key: ").append(ENCRYPTION_KEY).append("\n");
        
        return info.toString();
    }
    
    // Problema: Race condition en método de conteo
    public void incrementLoginAttempts() {
        // Problema: Operación no atómica
        int current = loginAttempts;
        try {
            Thread.sleep(1); // Simula procesamiento
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        loginAttempts = current + 1;
    }
    
    // Problema: Método que permite SQL injection
    public boolean isAuthorized(String userId, String resource) {
        // Problema: Simula query vulnerable
        String query = "SELECT permissions FROM user_permissions WHERE user_id = '" + 
                      userId + "' AND resource = '" + resource + "'";
        
        // Problema: No valida parámetros
        if (userId.contains("' OR '1'='1")) {
            return true; // Problema: Simula SQL injection exitoso
        }
        
        return false;
    }
    
    // Problema: Método que no valida entrada
    public void processUserInput(String input) {
        // Problema: Ejecuta código sin validación
        if (input.startsWith("eval:")) {
            String code = input.substring(5);
            // Problema: Simula ejecución de código arbitrario
            System.out.println("Executing: " + code);
        }
        
        // Problema: Escribir entrada del usuario directamente
        try {
            FileWriter writer = new FileWriter("user_input.log", true);
            writer.write(input + "\n");
            writer.close();
        } catch (IOException e) {
            // Problema: No maneja errores
        }
    }
    
    // Problema: Método con información de debug en producción
    public String debugUser(String username) {
        StringBuilder debug = new StringBuilder();
        debug.append("DEBUG INFO FOR: ").append(username).append("\n");
        debug.append("Password hash: ").append(hashPassword("defaultPassword")).append("\n");
        debug.append("Session token: ").append(generateSessionToken()).append("\n");
        debug.append("System info: ").append(getSystemInfo()).append("\n");
        
        // Problema: Información sensible en logs de debug
        return debug.toString();
    }
    
    // Problema: Hardcoded backdoor
    public boolean backdoorAccess(String code) {
        // Problema: Backdoor con código hardcodeado
        return "ADMIN_OVERRIDE_2024".equals(code);
    }
    
    // Problema: Método que no protege recursos
    public static void resetLoginAttempts() {
        // Problema: Método público que permite reset sin autenticación
        loginAttempts = 0;
    }
}
