package com.pragma.plazacomidas.mall.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String OWNER = "OWNER";
    private static final String EMPLOYEE = "EMPLOYEE";
    private static final String CLIENT = "CLIENT";
    private static final String ADMIN = "ADMIN";
    private static final String ORDER_PATH = "/api/v1/order/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/restaurant/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/restaurant/**").hasRole(ADMIN)
                .antMatchers(HttpMethod.POST, "/api/v1/plate/**").hasRole(OWNER)
                .antMatchers(HttpMethod.PATCH, "/api/v1/plate/**").hasRole(OWNER)
                .antMatchers(HttpMethod.POST, ORDER_PATH).hasRole(CLIENT)
                .antMatchers(HttpMethod.GET, "/api/v1/order/efficiency").hasRole(OWNER)
                .antMatchers(HttpMethod.GET, ORDER_PATH).hasRole(EMPLOYEE)
                .antMatchers(HttpMethod.PATCH, "/api/v1/order/*/cancel").hasRole(CLIENT)
                .antMatchers(HttpMethod.PATCH, ORDER_PATH).hasRole(EMPLOYEE)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
