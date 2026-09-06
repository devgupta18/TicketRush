package com.ticketrush.booking_service.service;

import com.ticketrush.booking_service.dto.AuthResponseDTO;
import com.ticketrush.booking_service.entity.User;
import com.ticketrush.booking_service.exception.EmailAlreadyExistsException;
import com.ticketrush.booking_service.exception.InvalidCredentialsException;
import com.ticketrush.booking_service.repository.UserRepository;
import com.ticketrush.booking_service.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO registerUser(String email, String password, String name) {
        if(userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        User createdUser = userRepository.save(user);

        String token = jwtUtil.generateToken(createdUser.getUserId());

        return new AuthResponseDTO(token);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new InvalidCredentialsException("Credentials Invalid!!!"));
    }

    public AuthResponseDTO loginUser(String email, String password) {
        User user = getUser(email);
        if(!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Credentials Invalid!!!");
        }
        return new AuthResponseDTO(jwtUtil.generateToken(user.getUserId()));
    }
}
