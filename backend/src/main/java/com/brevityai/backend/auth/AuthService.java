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

    public User register (String username, String password) {

        if (userRepository.findByUsername(username).isPresent())
            throw new RuntimeException("Username already taken");

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

//        if (userRepository.findByUsername(username).isEmpty() ||
//                !passwordEncoder.matches(password, user.getPassword()))
//            throw new RuntimeException("Username or password is invalid");

        return user;
    }

}
