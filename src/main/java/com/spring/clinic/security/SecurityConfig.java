package com.spring.clinic.security;
// Developed by Omar Abou Serieh - 2025
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/api/work-schedule/doc/**").hasRole("DOCTOR")
                        .requestMatchers("/api/work-schedule/all/**").hasAnyRole("DOCTOR", "ADMIN","PATIENT")
                        .requestMatchers("/api/admin-appointments").hasRole("ADMIN")
                        .requestMatchers("/api/admin-specialty/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin-users/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin-notification/**").hasRole("ADMIN")
                        .requestMatchers("/api/available-slots/doc/**").hasRole("DOCTOR")
                        .requestMatchers("/api/available-slots/slot/**").hasAnyRole("DOCTOR","PATIENT")
                        .requestMatchers("/api/users/doctor").hasRole("DOCTOR")
                        .requestMatchers("/api/users/patient").hasRole("PATIENT")
                        .requestMatchers("/api/users/public/**").hasAnyRole("DOCTOR","PATIENT")
                        .requestMatchers("/api/patient-record/rec/**").hasRole("DOCTOR")
                        .requestMatchers("/api/patient-record/pat/**").hasRole("PATIENT")
                        .requestMatchers("/api/doctor-details/pub/**").hasAnyRole("DOCTOR","PATIENT","ADMIN")
                        .requestMatchers("/api/doctor-details/my/**").hasRole("DOCTOR")
                        .requestMatchers("/api/appointment/doc").hasRole("DOCTOR").
                        requestMatchers("/api/notification/**").hasAnyRole("DOCTOR","PATIENT","ADMIN")
                        .requestMatchers("/api/appointment/pat/**").hasRole("PATIENT")
                        .requestMatchers("/api/appointment/pub/**").hasAnyRole("DOCTOR","PATIENT","ADMIN")
                        .requestMatchers("/api/specialty/**").hasAnyRole("DOCTOR","PATIENT","ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
