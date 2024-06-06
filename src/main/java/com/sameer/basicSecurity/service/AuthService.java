package com.sameer.basicSecurity.service;

import com.sameer.basicSecurity.model.Role;
import com.sameer.basicSecurity.model.User;
import com.sameer.basicSecurity.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sameer.basicSecurity.config.JwtService;
import com.sameer.basicSecurity.dto.AuthRequest;
import com.sameer.basicSecurity.dto.AuthResponse;
import com.sameer.basicSecurity.dto.RegisterRequest;
import com.sameer.basicSecurity.model.RefreshToken;
import com.sameer.basicSecurity.model.User;
import com.sameer.basicSecurity.repository.RefreshTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenRepository refreshTokenRepository;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
    }
@Transactional
    public User signup(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Role.USER)
                .build();


        return userRepository.save(user);
    }

    public User authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        return userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found in Db")
        );
    }



//    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        final String  refreshToken;
//        final String userEmail;
//        if (authHeader == null  || !authHeader.startsWith("Bearer ")) {
//            return;
//
//        }
//        refreshToken = authHeader.substring(7);
//        userEmail = jwtService.extractUsername(refreshToken);
//
//        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            var user = this.userRepository.findByEmail(userEmail).orElseThrow();
//
//            if (jwtService.isTokenValid(refreshToken,user)) {
//                var accessToken = jwtService.generateToken(user);
//                var authResponse = AuthResponse.builder()
//                        .accessToken(accessToken)
//                        .refreshToken(refreshToken)
//                        .build();
//                new ObjectMapper().writeValue(response.getOutputStream(),authResponse);
//            }
//        }
//    }
}
