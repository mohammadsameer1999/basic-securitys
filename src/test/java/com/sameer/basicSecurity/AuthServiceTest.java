package com.sameer.basicSecurity;

import com.sameer.basicSecurity.dto.AuthRequest;
import com.sameer.basicSecurity.model.Role;
import com.sameer.basicSecurity.model.User;
import com.sameer.basicSecurity.repository.UserRepository;
import com.sameer.basicSecurity.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setEmail("s@gmail.com");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setRoles(Role.USER);  // Ensure role is set correctly
        userRepository.save(user);
    }

    @Test
    public void testAuthenticate() {
        AuthRequest request = new AuthRequest();
        request.setEmail("s@gmail.com");
        request.setPassword("12345");

        User user = authService.authenticate(request);

        assertNotNull(user);
        assertEquals("s@gmail.com", user.getEmail());
        assertEquals(Role.USER,user.getRoles());
    }
}
