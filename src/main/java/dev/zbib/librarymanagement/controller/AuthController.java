package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.AuthResponse;
import dev.zbib.librarymanagement.dto.LoginRequest;
import dev.zbib.librarymanagement.dto.RegisterRequest;
import dev.zbib.librarymanagement.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
