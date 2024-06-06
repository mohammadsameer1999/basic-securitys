package com.sameer.basicSecurity.controller;


import com.sameer.basicSecurity.config.JwtService;
import com.sameer.basicSecurity.dto.AuthRequest;
import com.sameer.basicSecurity.dto.AuthResponse;
import com.sameer.basicSecurity.dto.RefreshTokenRequestDTO;
import com.sameer.basicSecurity.dto.RegisterRequest;
import com.sameer.basicSecurity.model.RefreshToken;
import com.sameer.basicSecurity.model.User;
import com.sameer.basicSecurity.service.AuthService;
import com.sameer.basicSecurity.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
        Logger logger = LoggerFactory.getLogger(AuthController.class);
    public AuthController(AuthService authService, JwtService jwtService, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User registeredUser = authService.signup(request);
        return ResponseEntity.ok(registeredUser);
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticateAndGetToken(@RequestBody AuthRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User authenticatedUser = authService.authenticate(request);
            String jwtToken = jwtService.generateToken(authenticatedUser);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(request.getEmail());
            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken.getToken())
                    .expiresIn(jwtService.getExpirationTime())
                    .build();

            response.put("status", 200);
            response.put("message", "success");
            response.put("doc", authResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", 404);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            String requestRefreshToken = refreshTokenRequestDTO.getRefreshToken();
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(refreshToken -> {
                        String token = jwtService.generateToken(refreshToken.getUser());
                        AuthResponse authResponse = AuthResponse.builder()
                                .accessToken(token)
                                .refreshToken(requestRefreshToken)
                                .expiresIn(jwtService.getExpirationTime())
                                .build();
                        response.put("status", 200);
                        response.put("message", "success");
                        response.put("data", authResponse);
                        return ResponseEntity.ok(response);
                    })
                    .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDTO request) {
        Map<String,Object> response = new HashMap<>();
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok().body("Successfully logged out");
    }
}
