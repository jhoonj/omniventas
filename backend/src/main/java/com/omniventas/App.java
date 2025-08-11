package com.omniventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.omniventas")
public class App {
    public static void main(String[] args) {

        String rawPassword = "admin"; // <-- tu contraseña real
        String hash = "$2a$10$gQ0s6GrNvDNp4zYvEbm0nOxF4CV7Y1a7phIXPmxAY5G8SkSO6KNlG";

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(rawPassword, hash);
        System.out.println("¿Coincide? " + matches);

         hash = encoder.encode(rawPassword);
        System.out.println("Nuevo hash: " + hash);


        SpringApplication.run(App.class, args);
    }
}