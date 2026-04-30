package com.brevityai.backend.auth;

import com.brevityai.backend.user.User;
import com.brevityai.backend.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register (String username, String email, String password) {

        if (userRepository.findByEmail(email).isPresent())
            throw new RuntimeException("Email address already in use");

        if (userRepository.findByUsername(username).isPresent())
            throw new RuntimeException("Username is already taken");



        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);
    }

    public void login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

    }

}
