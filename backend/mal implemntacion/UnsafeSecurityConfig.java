package com.example.demo.config;

import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// ❌ CONFIGURACIÓN DE SEGURIDAD PELIGROSA ❌

@Configuration
public class UnsafeSecurityConfig {
    
    // ❌ Deshabilitar TODA la seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()  // ❌ Sin protección CSRF
            .authorizeRequests()
            .anyRequest().permitAll()  // ❌ Permitir TODO sin autenticación
            .and()
            .headers().frameOptions().disable()  // ❌ Permitir frames (clickjacking)
            .and()
            .sessionManagement().maximumSessions(-1); // ❌ Sesiones ilimitadas
            
        return http.build();
    }
    
    // ❌ Bean con configuración insegura
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // ❌ Sin encriptación de contraseñas
    }
    
    // ❌ Configuración de CORS peligrosa
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // ❌ Permitir cualquier origen
        configuration.setAllowedMethods(Arrays.asList("*")); // ❌ Permitir todos los métodos
        configuration.setAllowedHeaders(Arrays.asList("*")); // ❌ Permitir todos los headers
        configuration.setAllowCredentials(true); // ❌ Con allowedOrigins("*") es peligroso
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

// ❌ OTRA CONFIGURACIÓN PROBLEMÁTICA ❌

@Configuration
class BadDatabaseConfig {
    
    // ❌ Configuración de BD con credenciales hardcodeadas
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://production-server:3306/sensitive_data"); // ❌ URL hardcodeada
        config.setUsername("root"); // ❌ Usuario root
        config.setPassword("password123"); // ❌ Contraseña débil hardcodeada
        config.setMaximumPoolSize(1000); // ❌ Pool demasiado grande
        config.setConnectionTimeout(0); // ❌ Sin timeout
        config.setValidationTimeout(0); // ❌ Sin validación
        
        return new HikariDataSource(config);
    }
    
    // ❌ JPA con configuración insegura
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create-drop"); // ❌ En producción!
        jpaProperties.put("hibernate.show_sql", "true"); // ❌ Mostrar SQL en logs
        jpaProperties.put("hibernate.format_sql", "true"); // ❌ Logs detallados
        jpaProperties.put("hibernate.use_sql_comments", "true"); // ❌ Comentarios en SQL
        jpaProperties.put("logging.level.org.hibernate.SQL", "DEBUG"); // ❌ Debug en prod
        
        em.setJpaProperties(jpaProperties);
        return em;
    }
}
