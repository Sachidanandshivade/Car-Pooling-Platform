package com.CarPooling.CarPoolingPlatform.controller;

import com.CarPooling.CarPoolingPlatform.dto.ApiResponse;
import com.CarPooling.CarPoolingPlatform.dto.LoginRequest;
import com.CarPooling.CarPoolingPlatform.dto.RegisterRequest;
import com.CarPooling.CarPoolingPlatform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);
        return ResponseEntity.ok(new ApiResponse(message, null, LocalDateTime.now()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new ApiResponse("Login successful", token, LocalDateTime.now()));
    }
}
