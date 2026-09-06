package com.ticketrush.booking_service.controller;

import com.ticketrush.booking_service.dto.AuthResponseDTO;
import com.ticketrush.booking_service.dto.LoginRequestDTO;
import com.ticketrush.booking_service.dto.RegisterRequestDTO;
import com.ticketrush.booking_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.registerUser(registerRequestDTO.email(), registerRequestDTO.password(), registerRequestDTO.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDTO);
    }

    @PostMapping("/login")
    public  ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        AuthResponseDTO authResponseDTO = authService.loginUser(loginRequestDTO.email(), loginRequestDTO.password());
        return ResponseEntity.status(HttpStatus.OK).body(authResponseDTO);
    }
}
