package com.apartment.controller;

import com.apartment.api.ApiResponse;
import com.apartment.model.User;
import com.apartment.security.JwtTokenProvider;
import com.apartment.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping({"/auth", "/api/v1/auth"})
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            logger.info("Attempting login for user: {}", username);
            
            if (username == null || password == null) {
                logger.error("Login failed: Username or password is null");
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名和密码不能为空"));
            }
            
            // 先检查用户是否存在
            try {
                userService.findByUsername(username);
            } catch (EntityNotFoundException e) {
                logger.error("Login failed: User not found: {}", username);
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
            }

            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = tokenProvider.generateAccessToken(authentication);

                User user = userService.findByUsername(username);
                
                Map<String, Object> result = new HashMap<>();
                result.put("token", token);
                result.put("user", user);
                
                logger.info("Login successful for user: {}", username);
                return ResponseEntity.ok(ApiResponse.success("登录成功", result));
            } catch (BadCredentialsException e) {
                logger.error("Login failed: Bad credentials for user: {}", username);
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
            }
        } catch (Exception e) {
            logger.error("Login failed: Unexpected error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("登录失败: " + e.getMessage()));
        }
    }

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        logger.debug("Test endpoint called");
        return ResponseEntity.ok(ApiResponse.success("测试成功", "Auth API 工作正常"));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try {
            logger.debug("Attempting to register user: {}", user.getUsername());
            
            // 检查用户名是否已存在
            if (userService.existsByUsername(user.getUsername())) {
                logger.debug("Registration failed: Username already exists: {}", user.getUsername());
                return ResponseEntity.badRequest().body(ApiResponse.error("用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (user.getEmail() != null && userService.existsByEmail(user.getEmail())) {
                logger.debug("Registration failed: Email already exists: {}", user.getEmail());
                return ResponseEntity.badRequest().body(ApiResponse.error("邮箱已被注册"));
            }

            // 检查手机号是否已存在
            if (user.getPhone() != null && userService.existsByPhone(user.getPhone())) {
                logger.debug("Registration failed: Phone already exists: {}", user.getPhone());
                return ResponseEntity.badRequest().body(ApiResponse.error("手机号已被注册"));
            }

            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // 保存用户
            User savedUser = userService.save(user);
            
            logger.debug("Registration successful for user: {}", user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("注册成功", savedUser));
        } catch (Exception e) {
            logger.error("Registration failed: Unexpected error", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("注册失败: " + e.getMessage()));
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            logger.debug("Getting user info for user: {}", username);
            
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", user));
        } catch (Exception e) {
            logger.error("Get user info failed: Unexpected error", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT是无状态的，客户端只需要删除token即可
        logger.debug("Logout called");
        return ResponseEntity.ok(ApiResponse.success("退出成功"));
    }
} 