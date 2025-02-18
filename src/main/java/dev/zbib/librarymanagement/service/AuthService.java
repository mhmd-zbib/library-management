package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.dto.AuthResponse;
import dev.zbib.librarymanagement.dto.LoginRequest;
import dev.zbib.librarymanagement.dto.RegisterRequest;
import dev.zbib.librarymanagement.entity.Role;
import dev.zbib.librarymanagement.entity.User;
import dev.zbib.librarymanagement.logging.LogLevel;
import dev.zbib.librarymanagement.logging.LoggableOperation;
import dev.zbib.librarymanagement.repository.RoleRepository;
import dev.zbib.librarymanagement.repository.UserRepository;
import dev.zbib.librarymanagement.security.JwtUtil;
import dev.zbib.librarymanagement.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dev.zbib.librarymanagement.dto.Role.PATRON;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    @LoggableOperation(
            operationType = "USER_REGISTRATION",
            description = "Processing new user registration",
            level = LogLevel.INFO,
            includeParameters = true,
            includeResult = false,
            maskSensitiveData = true
    )
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role userRole = roleRepository.findByName(String.valueOf(PATRON))
                .orElseThrow(() -> new IllegalStateException("Default role not found"));
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);
        String token = jwtUtil.generateToken(securityUser);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole()
                        .getName())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword()));

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(securityUser);

        return AuthResponse.builder()
                .token(token)
                .email(securityUser.getUsername())
                .role(securityUser.getUser()
                        .getRole()
                        .getName())
                .build();
    }
}