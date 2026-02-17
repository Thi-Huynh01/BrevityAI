package com.brevityai.backend.auth;

import com.brevityai.backend.auth.dto.AuthRequest;
import com.brevityai.backend.auth.jwt.JWTService;
import com.brevityai.backend.user.User;
import com.brevityai.backend.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JWTService jWTService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    // Register endpoint
    @PostMapping("/register")
    public User register(@RequestBody Map<String, String> body) {
        return authService.register(
                body.get("username"),
                body.get("password")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jWTService.generateToken(user.getUsername());

        return ResponseEntity.ok(Map.of("token", token));

    }
}
