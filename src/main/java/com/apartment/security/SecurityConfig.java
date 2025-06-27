package com.apartment.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security配置类
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * CORS配置
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }

    /**
     * 安全过滤器链
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/api/v1/auth/**").permitAll()
                .antMatchers("/hello").permitAll()
                .antMatchers("/test").permitAll()
                .antMatchers("/files/**").permitAll()
                .antMatchers("/uploads/**").permitAll() // 允许访问上传的文件
                .antMatchers("/api/v1/files/**").permitAll() // 允许访问API文件端点
                .antMatchers("/api/v1/upload/**").hasAnyRole("USER", "ADMIN", "LANDLORD") // 允许已登录用户上传文件
                .antMatchers("/api/v1/landlords/test/**").permitAll() // 允许测试接口
                .antMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .antMatchers("/dashboard/**").permitAll() // 暂时开放仪表盘接口，方便测试
                .antMatchers("/api/v1/dashboard/**").permitAll()
                .antMatchers(HttpMethod.GET, "/properties/**", "/rooms/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/properties/**", "/api/v1/rooms/**").permitAll()
                .antMatchers(HttpMethod.POST, "/properties/**", "/rooms/**").hasAnyRole("ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.POST, "/api/v1/properties/**", "/api/v1/rooms/**").hasAnyRole("ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.PUT, "/properties/**", "/rooms/**").hasAnyRole("ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.PUT, "/api/v1/properties/**", "/api/v1/rooms/**").hasAnyRole("ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.DELETE, "/properties/**", "/rooms/**").hasAnyRole("ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.DELETE, "/api/v1/properties/**", "/api/v1/rooms/**").hasAnyRole("ADMIN", "LANDLORD")
                // 账单管理API权限配置
                .antMatchers(HttpMethod.GET, "/api/v1/bills/**").hasAnyRole("ADMIN", "LANDLORD", "USER")
                .antMatchers(HttpMethod.POST, "/api/v1/bills/**").hasAnyRole("ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.PUT, "/api/v1/bills/**").hasAnyRole("ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.DELETE, "/api/v1/bills/**").hasAnyRole("ADMIN", "LANDLORD")
                // 用户API权限配置
                .antMatchers(HttpMethod.GET, "/api/v1/users/available-rooms").permitAll() // 允许所有人查看可租房源
                .antMatchers(HttpMethod.POST, "/api/v1/users/room-applications").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users/room-applications").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/v1/users/room-applications/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users/rentals").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users/rent-bills").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users/utility-bills").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/v1/users/me").hasAnyRole("USER", "ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.PUT, "/api/v1/users/me").hasAnyRole("USER", "ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.POST, "/api/v1/users/avatar").hasAnyRole("USER", "ADMIN", "LANDLORD")
                .antMatchers(HttpMethod.GET, "/api/v1/users/dashboard/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/api/v1/users/**").hasAnyRole("USER", "ADMIN", "LANDLORD")
                .anyRequest().authenticated();

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
} 