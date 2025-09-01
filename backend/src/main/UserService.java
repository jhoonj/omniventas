package com.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {
    
    private String hardcodedPassword = "admin123"; // Problema: Password hardcodeado
    
    // Problema: Falta JavaDoc
    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        users.add("john");
        users.add("jane");
        return users;
    }
    
    // Problema: Método sin tests, falta JavaDoc
    public boolean validateUser(String username, String password) {
        if(username == null || password == null) {
            return false;
        }
        return username.equals("admin") && password.equals(hardcodedPassword);
    }
    
    // Problema: Nomenclatura incorrecta (camelCase)
    public String get_user_email(String username) {
        return username + "@company.com";
    }
    
    // Problema: Método complejo sin documentación ni tests
    public Optional<String> processUserData(List<String> usernames) {
        if (usernames == null || usernames.isEmpty()) {
            return Optional.empty();
        }
        
        StringBuilder result = new StringBuilder();
        for (String user : usernames) {
            if (user != null && !user.trim().isEmpty()) {
                result.append(user.toUpperCase()).append(",");
            }
        }
        
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }
        
        return Optional.of(result.toString());
    }
}
