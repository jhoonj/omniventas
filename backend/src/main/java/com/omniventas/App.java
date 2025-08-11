package com.omniventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.omniventas")
public class App {
    public static void main(String[] args) {
   SpringApplication.run(App.class, args);
    }
}