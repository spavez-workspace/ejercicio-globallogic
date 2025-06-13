package com.globallogic.ejercicio.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class WebSecurityConfig {
	
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    
    public JwtFilter authenticationJwtTokenFilter() {
        return new JwtFilter();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF
                .cors(cors -> cors.disable()) // Deshabilita CORS
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
	                        .requestMatchers(new AntPathRequestMatcher("/api/users/sign-up")).permitAll()
	                        .requestMatchers(new AntPathRequestMatcher("/api/users/login")).permitAll()
	                        .requestMatchers(new AntPathRequestMatcher("/api/users/loginByRequestBody")).permitAll()
	                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
	                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui.html")).permitAll()
	                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
	                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
	                        .requestMatchers(new AntPathRequestMatcher("/api/private")).authenticated()
	                        .anyRequest().authenticated()
                );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}